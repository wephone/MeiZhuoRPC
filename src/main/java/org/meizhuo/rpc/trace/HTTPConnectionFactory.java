package org.meizhuo.rpc.trace;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.HttpRequestEncoder;
import io.netty.handler.codec.http.HttpResponseDecoder;
import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.meizhuo.rpc.client.RPCRequestHandler;
import org.meizhuo.rpc.core.RPC;

public class HTTPConnectionFactory extends BasePooledObjectFactory<Channel> {

    private EventLoopGroup group=new NioEventLoopGroup();

    @Override
    public Channel create() throws Exception {
        Bootstrap b=new Bootstrap();
        b.group(group)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE,true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        //HTTP 编解码器 不接受返回 无需回调器
                        socketChannel.pipeline().addLast(new HttpResponseDecoder());
                        socketChannel.pipeline().addLast(new HttpRequestEncoder());
                    }
                });
        String zipkinUrl=RPC.getTraceConfig().getZipkinUrl();
        String[] zipkin=zipkinUrl.split(":");
        String zipkinIp=zipkin[0];
        Integer zipkinPort=Integer.valueOf(zipkin[1]);
        ChannelFuture f=b.connect(zipkinIp,zipkinPort).sync();
        return f.channel();
    }

    @Override
    public PooledObject<Channel> wrap(Channel channel) {
        return new DefaultPooledObject<Channel>(channel);
    }

    @Override
    public void destroyObject(PooledObject<Channel> p) throws Exception {
        p.getObject().close();
    }

}
