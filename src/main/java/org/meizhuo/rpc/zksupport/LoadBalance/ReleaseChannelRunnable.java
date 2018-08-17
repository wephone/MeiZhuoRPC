package org.meizhuo.rpc.zksupport.LoadBalance;

import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import org.meizhuo.rpc.client.RPCRequestNet;
import org.meizhuo.rpc.core.RPC;

/**
 * Created by wephone on 18-1-11.
 * 释放资源 关闭通道
 */
@Deprecated
public class ReleaseChannelRunnable implements Runnable{

    String ip;

    public ReleaseChannelRunnable(String ip) {
        this.ip = ip;
    }

    @Override
    public void run() {
//        try {
//            Channel channel=RPCRequestNet.getInstance().IPChannelMap.get(ip).getChannel();
//            //判空再释放 防止还未连接该ip时就移除连接
//            if (channel!=null) {
//                long overTime= RPC.getClientConfig().getOvertime();
//                Thread.sleep(overTime);//等待超时时间 保证前面选中该ip的消费者没有RPC请求在发往该节点地址等待返回
//                RPCRequestNet.getInstance().IPChannelMap.get(ip).getChannel().close().sync();
//                EventLoopGroup group=RPCRequestNet.getInstance().IPChannelMap.get(ip).getGroup();
//                if (group!=null) {
//                    RPCRequestNet.getInstance().IPChannelMap.get(ip).getGroup().shutdownGracefully();
//                }
//            }
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        //这个IP没人引用时 去除这个IP的映射关系
//        RPCRequestNet.getInstance().IPChannelMap.remove(ip);
//        System.out.println("已关闭"+ip+"并释放资源");
    }
}
