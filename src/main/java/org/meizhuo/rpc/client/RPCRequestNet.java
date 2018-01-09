package org.meizhuo.rpc.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import org.apache.zookeeper.ZooKeeper;
import org.meizhuo.rpc.core.RPC;
import org.meizhuo.rpc.zksupport.LoadBalance.LoadBalance;
import org.meizhuo.rpc.zksupport.LoadBalance.Polling;
import org.meizhuo.rpc.zksupport.ZKConnect;
import org.meizhuo.rpc.zksupport.service.ServiceInfo;
import org.meizhuo.rpc.zksupport.service.ZKClientService;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


/**
 * Created by wephone on 17-12-27.
 * 单例RPC请求类 调用端通过此单例进行对提供者端的请求
 * 有些属性需要是全局的例如requestLockMap 所以这里是单例的
 */
public class RPCRequestNet {

    public Map requestLockMap=new ConcurrentHashMap<String,RPCRequest>();//全局map 每个请求对应的锁 用于同步等待每个异步的RPC请求
    public Lock connectlock=new ReentrantLock();//阻塞等待连接成功的锁
    public Condition connectCondition=connectlock.newCondition();
    //服务名称 映射 服务信息类
    public ConcurrentHashMap<String,ServiceInfo> serviceNameInfoMap;
    //IP地址 映射 对应的NIO Channel及其引用次数
    public ConcurrentHashMap<String,IPChannelInfo> IPChannelMap;
    private LoadBalance loadBalance;
    private static RPCRequestNet instance;

    private RPCRequestNet() {
        //TODO 后续改为根据配置选择负载均衡策略
        loadBalance=new Polling();
    }

    //负载均衡获取对应IP 端口后发起连接
    private void connect(String ip){
        String[] IPArr=ip.split(":");
        String host=IPArr[0];
        Integer port=Integer.valueOf(IPArr[2]);
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
                        socketChannel.pipeline().addLast(new LineBasedFrameDecoder(2048));//以换行符分包 防止粘包半包 2048为最大长度 到达最大长度没出现换行符则抛出异常
                        socketChannel.pipeline().addLast(new StringDecoder());//将接收到的对象转为字符串
                        //添加相应回调处理和编解码器
                        socketChannel.pipeline().addLast(new RPCRequestHandler());
                    }
                });
        try {
            //TODO 从自定义标签配置中读取参数 启动网络连接
            ChannelFuture f=b.connect(host,port).sync();
//            f.channel().closeFuture().sync();//会造成阻塞构造方法
            f.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture channelFuture) throws Exception {
                    /*
                     * sync内部会在NIO线程wait阻塞 导致这个接口回调时阻塞和唤醒可能都在同一线程造成死锁
                     * 不论是用户直接关闭或者eventLoop的轮询状态关闭，都会在eventLoop的线程内完成notify动作，所以不要在IO线程内调用future对象的sync或者await方法
                     * 应用程序代码都是编写的channelHandler，而channelHandler是在eventLoop的线程内执行的，所以是不能在channelHandler中调用sync或者await方法的
                     * channel.closeFuture()不做任何操作，只是简单的返回channel对象中的closeFuture对象，对于每个Channel对象，都会有唯一的一个CloseFuture，用来表示关闭的Future，
                     所有执行channel.closeFuture().sync()就是执行的CloseFuture的sync方法，从上面的解释可以知道，这步是会将当前线程阻塞在CloseFuture上
                     */
//                    f.channel().closeFuture().sync();//应用程序会一直等待，直到channel关闭 这句会造成死锁异常BlockingOperationException
                }
            });

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    //单例模式 避免重复连接 构造方法中进行连接操作
    public static RPCRequestNet getInstance(){
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
            String requestJson= null;
            try {
                requestJson = RPC.requestEncode(request);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
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
