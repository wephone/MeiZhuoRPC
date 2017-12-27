package org.meizhuo.rpc.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;


/**
 * Created by wephone on 17-12-27.
 */
public class RPCRequestNet {


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
        //启动网络连接
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

    }
}
