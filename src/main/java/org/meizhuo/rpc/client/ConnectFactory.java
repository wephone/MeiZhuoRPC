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
import org.meizhuo.rpc.core.RPC;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ConnectFactory extends BasePooledObjectFactory<Channel> {

    private String ip;
    private Integer port;
    //netty线程组 同一个服务的连接池内各个连接共用
    private EventLoopGroup group=new NioEventLoopGroup();
//    private Logger logger=LoggerFactory.getLogger(ConnectFactory.class);

    public ConnectFactory(String ip, Integer port) {
        this.ip = ip;
        this.port = port;
    }

    public EventLoopGroup getGroup() {
        return group;
    }

    @Override
    public Channel create() throws Exception {
        //启动辅助类 用于配置各种参数
        Bootstrap b=new Bootstrap();
        b.group(group)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY,true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        socketChannel.pipeline().addLast(RPC.getClientConfig().getDecoder());//自定义协议编码器解码器
                        socketChannel.pipeline().addLast(RPC.getClientConfig().getEncoder());
                        //添加相应回调处理和编解码器
                        socketChannel.pipeline().addLast(new RPCRequestHandler());
                    }
                });
        ChannelFuture f=b.connect(ip,port).sync();
        //            f.addListener(new ChannelFutureListener() {
//                @Override
//                public void operationComplete(ChannelFuture channelFuture) throws Exception {
                    /*
                     * sync内部会在NIO线程wait阻塞 导致这个接口回调时阻塞和唤醒可能都在同一线程造成死锁
                     * 不论是用户直接关闭或者eventLoop的轮询状态关闭，都会在eventLoop的线程内完成notify动作，所以不要在IO线程内调用future对象的sync或者await方法
                     * 应用程序代码都是编写的channelHandler，而channelHandler是在eventLoop的线程内执行的，所以是不能在channelHandler中调用sync或者await方法的
                     * channel.closeFuture()不做任何操作，只是简单的返回channel对象中的closeFuture对象，对于每个Channel对象，都会有唯一的一个CloseFuture，用来表示关闭的Future，
                     所有执行channel.closeFuture().sync()就是执行的CloseFuture的sync方法，从上面的解释可以知道，这步是会将当前线程阻塞在CloseFuture上
                     */
//                    f.channel().closeFuture().sync();//应用程序会一直等待，直到channel关闭 这句会造成死锁异常BlockingOperationException
//                }
//            });

        System.out.println("pool create channel "+ip+":"+port);
//        logger.info("pool create channel "+ip+":"+port);
        return f.channel();
    }

    @Override
    public PooledObject<Channel> wrap(Channel channel) {
        return new DefaultPooledObject<Channel>(channel);
    }

    @Override
    public void destroyObject(PooledObject<Channel> p) throws Exception {
        System.out.println("destroy channel "+ip+":"+port);
        //销毁channel时释放资源 http://www.infoq.com/cn/articles/netty-elegant-exit-mechanism-and-principles
        p.getObject().close();
        //关闭链路 而不是关闭所有EventLoop线程和注册在该线程持有的多路复用器上所有的Channel
//        p.getObject().eventLoop().shutdownGracefully();
    }

}
