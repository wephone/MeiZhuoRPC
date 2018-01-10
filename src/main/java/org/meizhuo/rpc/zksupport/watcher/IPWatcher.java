package org.meizhuo.rpc.zksupport.watcher;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.meizhuo.rpc.zksupport.ZKConst;

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
         * 获取所有可用服务提供或者消费者子节点
         * 计算数量
         * 平衡消费者持有的长连接
         * 完成操作后再次注册
         */
        String path=watchedEvent.getPath();
        List<String> children=new ArrayList<>();
        try {
            children=zooKeeper.getChildren(path,this);
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //判断是提供者数量变了还是调用者数量变了
        if (path.contains(ZKConst.providersPath)){
//            MinConnectRandom.getInstance().setServerCount(children.size());
        }else if (path.contains(ZKConst.consumersPath)){
//            MinConnectRandom.getInstance().setClientCount(children.size());
        }

    }
}
