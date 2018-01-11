package org.meizhuo.rpc.zksupport.LoadBalance;

import org.meizhuo.rpc.client.RPCRequestNet;
import org.meizhuo.rpc.core.RPC;

/**
 * Created by wephone on 18-1-11.
 * 释放资源 关闭通道
 */
public class ReleaseChannelRunnable implements Runnable{

    String ip;

    public ReleaseChannelRunnable(String ip) {
        this.ip = ip;
    }

    @Override
    public void run() {
        try {
            long overTime= RPC.getClientConfig().getOvertime();
            Thread.sleep(overTime);//等待超时时间 保证前面选中该ip的消费者没有RPC请求在发往该节点地址等待返回
            RPCRequestNet.getInstance().IPChannelMap.get(ip).getChannel().close().sync();
            RPCRequestNet.getInstance().IPChannelMap.get(ip).getGroup().shutdownGracefully();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //这个IP没人引用时 去除这个IP的映射关系
        RPCRequestNet.getInstance().IPChannelMap.remove(ip);
    }
}
