package org.meizhuo.rpc.zksupport.LoadBalance;

import io.netty.channel.ChannelHandlerContext;
import org.meizhuo.rpc.client.IPChannelInfo;
import org.meizhuo.rpc.client.RPCRequestNet;
import org.meizhuo.rpc.zksupport.service.ServiceInfo;

import java.util.List;

/**
 * Created by wephone on 18-1-7.
 * 负载均衡轮询策略操作类 保证每个提供者都被连接使用到
 */
public class Polling implements LoadBalance{

    @Override
    public void balance(String serviceName) {

    }

    @Override
    public ChannelHandlerContext chooseChannel(String serviceName) {
        return null;
    }

    /**
     * 轮询策略 前面节点的提供者接满时才连接下一个节点的IP 所以需要持有的IP数量减少时 先去除掉后面接入的IP
     */
    public void removeLastConnectIP(ServiceInfo serviceInfo){
        int lastIndex=serviceInfo.getConnectIPList().size()-1;
        String IP=serviceInfo.getConnectIPList().get(lastIndex);
        //移除此服务对该IP的引用
        serviceInfo.getConnectIPList().remove(lastIndex);
        RPCRequestNet net=RPCRequestNet.getInstance();
        IPChannelInfo channelInfo=net.IPChannelMap.get(IP);
        //检查别的服务是否仍需此ip的连接 如果对应的引用为0 则没有其他服务需要次连接了则释放对应socketChannel
        if (channelInfo.decrementServiceQuoteNum()==0){
            //关闭通道 TODO disconnect和close什么区别 如何优雅退出netty客户端 应该是得用bootstrap退出
//            channelInfo.getChannelHandlerContext().disconnect();
//            workerGroup.shutdownGracefully();
            channelInfo.getChannelHandlerContext().close();
            net.IPChannelMap.remove(IP);
        }
    }
}
