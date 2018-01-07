package org.meizhuo.rpc.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import org.meizhuo.rpc.core.RPC;



/**
 * Created by wephone on 17-12-30.
 */
public class RPCResponseNet {

    public static void connect(){
        //netty主从线程模型
        EventLoopGroup bossGroup=new NioEventLoopGroup();
        EventLoopGroup workerGroup=new NioEventLoopGroup();
        try {
            //启动辅助类 用于配置各种参数
            ServerBootstrap b=new ServerBootstrap();
            b.group(bossGroup,workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG,1024)//输入连接指示（对连接的请求）的最大队列长度被设置为 backlog 参数。如果队列满时收到连接指示，则拒绝该连接。
                    .childHandler(new ChannelInitializer<SocketChannel>(){

                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline().addLast(new LineBasedFrameDecoder(2048));//以换行符分包 防止念包半包 2048为最大长度 到达最大长度没出现换行符则抛出异常
                            socketChannel.pipeline().addLast(new StringDecoder());//将接收到的对象转为字符串
                            socketChannel.pipeline().addLast(new RPCResponseHandler());
                        }
                    });//绑定IO事件处理类
            //绑定端口 同步等待成功
            ChannelFuture future=b.bind(RPC.getServerConfig().getPort()).sync();
            System.out.println("service start on port:"+RPC.getServerConfig().getPort());
            //同步等待服务端监听端口关闭 后面改用闭锁来阻塞提供者端
//            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            //释放资源退出
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

}
