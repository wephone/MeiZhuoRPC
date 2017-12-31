package org.meizhuo.rpc.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import org.meizhuo.rpc.core.RPC;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


/**
 * Created by wephone on 17-12-27.
 */
public class RPCRequestNet {

    public static Map requestLockMap=new ConcurrentHashMap<String,Condition>();;//全局map 每个请求对应的锁 用于同步等待每个异步的RPC请求
    public static Lock connectlock=new ReentrantLock();//阻塞等待连接成功的锁
    public static Condition connectCondition=connectlock.newCondition();
    private static RPCRequestNet instance;

    private RPCRequestNet() {
        //netty线程组
        EventLoopGroup group=new NioEventLoopGroup();
        //启动辅助类 用于配置各种参数
        Bootstrap b=new Bootstrap();
        b.group(group)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY,true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        socketChannel.pipeline().addLast(new LineBasedFrameDecoder(2048));//以换行符分包 防止念包半包 2048为最大长度 到达最大长度没出现换行符则抛出异常
                        socketChannel.pipeline().addLast(new StringDecoder());//将接收到的对象转为字符串
                        //添加相应回调处理和编解码器
                        socketChannel.pipeline().addLast(new RPCRequestHandler());
                    }
                });
        try {
            //TODO 从自定义标签配置中读取参数 启动网络连接
            ChannelFuture f=b.connect(ClientConfig.host,ClientConfig.port).sync();
//            f.channel().closeFuture().sync();//会造成阻塞构造方法
            f.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture channelFuture) throws Exception {
//                    f.channel().closeFuture().sync();//这句会造成死锁异常BlockingOperationException
                }
            });

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    //单例模式 避免重复连接 构造方法中进行连接操作
    public static RPCRequestNet connect(){
        if (instance==null){
            synchronized (RPCRequest.class){
                if (instance==null){
                    instance=new RPCRequestNet();
                }
            }
        }
        return instance;
    }

    //向实现端发送请求
    public void send(RPCRequest request){
//        System.out.println("Send RPC Thread:"+Thread.currentThread().getName());
        try {
            //判断连接是否已完成 只在连接启动时会产生阻塞
            if (RPCRequestHandler.channelCtx==null){
                connectlock.lock();
                //挂起等待连接成功
                System.out.println("正在等待连接实现端");
                connectCondition.await();
                connectlock.unlock();
            }
            //编解码对象为json 发送请求
            String requestJson= RPC.requestEncode(request);
            ByteBuf requestBuf= Unpooled.copiedBuffer(requestJson.getBytes());
            RPCRequestHandler.channelCtx.writeAndFlush(requestBuf);
            System.out.println("调用"+request.getRequestID()+"已发送");
            //挂起等待实现端处理完毕返回 TODO 后续配置超时时间
            synchronized (request) {
                //放弃对象锁 并阻塞等待notify
                request.wait();
            }
            System.out.println("调用"+request.getRequestID()+"接收完毕");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
