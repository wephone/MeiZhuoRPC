package org.meizhuo.rpc.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;


public class ConnectFactory extends BasePooledObjectFactory<Channel> {

    private String ip;
    private Integer port;

    public ConnectFactory(String ip, Integer port) {
        this.ip = ip;
        this.port = port;
    }

    @Override
    public Channel create() throws Exception {
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
        ChannelFuture f=b.connect(ip,port).sync();
        return f.channel();
    }

    @Override
    public PooledObject<Channel> wrap(Channel channel) {
        return new DefaultPooledObject<Channel>(channel);
    }

}
