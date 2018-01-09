package org.meizhuo.rpc.zksupport.LoadBalance;

import io.netty.channel.ChannelHandlerContext;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;
import org.meizhuo.rpc.client.IPChannelInfo;
import org.meizhuo.rpc.client.RPCRequestNet;
import org.meizhuo.rpc.zksupport.ZKTempZnodes;
import org.meizhuo.rpc.zksupport.service.ServiceInfo;
import org.meizhuo.rpc.zksupport.service.ZKClientService;
import org.meizhuo.rpc.zksupport.service.ZKServerService;
import org.meizhuo.rpc.zksupport.service.ZnodeType;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by wephone on 18-1-7.
 * 负载均衡轮询策略操作类 保证每个提供者都被连接使用到
 * 负载均衡类非单例
 */
public class Polling implements LoadBalance{

    @Override
    public void balance(ZooKeeper zooKeeper, String serviceName, List<String> znodes, ZnodeType type) {
        final String service=serviceName;
        Runnable runnable=new Runnable() {
            @Override
            public void run() {
                ReadWriteLock readWriteLock=new ReentrantReadWriteLock();
                //不存在则新建一个键值对 存在则不操作
                BalanceThreadPool.serviceLockMap.putIfAbsent(service,readWriteLock);
                BalanceThreadPool.serviceLockMap.get(service).writeLock().lock();
                ServiceInfo serviceInfo=RPCRequestNet.getInstance().serviceNameInfoMap.get(service);
                int oldConnectNum=getConnectNum(serviceInfo.getClientCount(),serviceInfo.getServerCount());
                if (type==ZnodeType.consumer){
                    serviceInfo.setClientCount(znodes.size());
                }else {
                    serviceInfo.setServerCount(znodes.size());
                }
                int newConnectNum=getConnectNum(serviceInfo.getClientCount(),serviceInfo.getServerCount());
                ZKClientService zkClientService=new ZKClientService(zooKeeper);
                ZKServerService zkServerService=new ZKServerService(zooKeeper);
                List<String> serverIPList= new ArrayList<>();
                try {
                    serverIPList = zkServerService.getServiceServerIP(service);
                } catch (KeeperException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                //TODO 不用考虑此次执行因为其他线程对连接数znode的修改 导致本线程无法新增或减少连接
                //只要触发的watcher次数正确 连接数未达到最大的就继续连接 这个最后一次平衡总能达到平衡连接
                if (newConnectNum>oldConnectNum){
                    //新应连接数大于原先的 则增加对服务端的连接
                    for (String ip:serverIPList){
                        //不增加自己已有的IP的连接数
                        if (!serviceInfo.getConnectIPSet().contains(ip)){
                            try {
                                //该ip是否符合连接
                                boolean add=zkClientService.addServiceServerConnectNum(serviceName,ip,newConnectNum);
                                if (add){
                                    serviceInfo.addConnectIP(ip);
                                    //增加IP被服务引用的次数
                                    RPCRequestNet.getInstance().IPChannelMap.get(ip).incrementServiceQuoteNum();
                                }
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }else {
                    //否则减少对服务端的连接
                }
                //写入新serviceInfo
                RPCRequestNet.getInstance().serviceNameInfoMap.put(service,serviceInfo);
                BalanceThreadPool.serviceLockMap.get(service).writeLock().unlock();
            }
        };
        BalanceThreadPool.execute(runnable);
    }

    private int getConnectNum(int clientNum,int serverNum){
        int connectNum=0;
        if (serverNum>=clientNum){
            connectNum=1;
        }else {
            connectNum=serverNum/clientNum;
            if ((serverNum%clientNum!=0){
                connectNum++;
            }
        }
        return connectNum;
    }

    @Override
    public ChannelHandlerContext chooseChannel(String serviceName) {
        return null;
    }

    /**
     * 轮询策略 前面节点的提供者接满时才连接下一个节点的IP 所以需要持有的IP数量减少时 先去除掉后面接入的IP
     */
    public void removeLastConnectIP(ServiceInfo serviceInfo){
        int lastIndex=serviceInfo.getConnectIPSet().size()-1;
        String IP=serviceInfo.getConnectIPSet().get(lastIndex);
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
