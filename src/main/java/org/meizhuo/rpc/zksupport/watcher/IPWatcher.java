package org.meizhuo.rpc.zksupport.watcher;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.meizhuo.rpc.core.RPC;
import org.meizhuo.rpc.zksupport.LoadBalance.LoadBalance;
import org.meizhuo.rpc.zksupport.ZKConst;
import org.meizhuo.rpc.zksupport.service.ZnodeType;

import java.util.ArrayList;
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
        String serviceName=pathArr[2];//第三个部分则为服务名
        try {
            List<String> children=zooKeeper.getChildren(path,this);
            LoadBalance loadBalance= RPC.getClientConfig().getLoadBalance();
            loadBalance.balance(zooKeeper,serviceName,children, ZnodeType.provider);
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
