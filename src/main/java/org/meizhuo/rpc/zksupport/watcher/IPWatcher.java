package org.meizhuo.rpc.zksupport.watcher;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.meizhuo.rpc.client.IPChannelInfo;
import org.meizhuo.rpc.client.RPCRequestNet;
import org.meizhuo.rpc.core.RPC;

import java.util.List;

/**
 * Created by wephone on 18-1-7.
 * 服务提供者和调用者的IP监控器 即监听服务的可用性
 */
public class IPWatcher implements Watcher{

    private ZooKeeper zooKeeper;

    public IPWatcher(ZooKeeper zooKeeper) {
        this.zooKeeper = zooKeeper;
    }

    @Override
    public void process(WatchedEvent watchedEvent) {
        /**
         * 监听到节点提供者IP节点变化时被调用
         * 调用后进行平衡操作
         */
        String path=watchedEvent.getPath();
        String[] pathArr=path.split("/");
        String serviceName=pathArr[3];//第四个部分则为服务名
        RPCRequestNet.getInstance().serviceLockMap.get(serviceName).writeLock().lock();
        System.out.println("providers changed...Lock write Lock");
        try {
            List<String> children=zooKeeper.getChildren(path,this);
            for (String ip:children){
                RPCRequestNet.getInstance().IPChannelMap.putIfAbsent(ip,new IPChannelInfo());
            }
            RPC.getClientConfig().getLoadBalance().changeIP(serviceName,children);
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        RPCRequestNet.getInstance().serviceLockMap.get(serviceName).writeLock().unlock();
    }
}
