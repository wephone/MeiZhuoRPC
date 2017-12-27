package org.meizhuo.rpc.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


/**
 * Created by wephone on 17-12-27.
 */
public class RPCRequestNet {

    private static boolean isConnectSuccess=false;
    public static Map requestLockMap=new ConcurrentHashMap<String,Condition>();;//全局map 每个请求对应的锁 用于同步等待每个异步的RPC请求
    private static Lock connectlock=new ReentrantLock();//阻塞等待连接成功的锁
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
                        //添加相应回调处理和编解码器
                        socketChannel.pipeline().addLast(new RPCRequestHandler());
                    }
                });
        //TODO 启动网络连接
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
        try {
            //判断连接是否已完成 只在连接启动时会产生阻塞
            if (!isConnectSuccess){
                connectlock.lock();
                //挂起等待连接成功
                System.out.println("正在等待连接实现端");
                connectCondition.wait();
                connectlock.unlock();
            }
            //TODO 发送请求
            System.out.print("调用"+request.getRequestID()+"已发送");
            //挂起等待实现端处理完毕返回 TODO 后续配置超时时间
            requestLockMap.get(request.getRequestID()).wait();
            System.out.print("调用"+request.getRequestID()+"接收完毕");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
