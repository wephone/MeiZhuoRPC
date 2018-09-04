package org.meizhuo.rpc.zksupport.watcher;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.meizhuo.rpc.zksupport.ZKConst;

import java.io.IOException;

/**
 * 监听session过期 用于段先重连
 */
public class SessionExpiredWatcher implements Watcher {

    private String host;

    public SessionExpiredWatcher(String host) {
        this.host = host;
    }

    @Override
    public void process(WatchedEvent watchedEvent) {
        if (watchedEvent.getState()== Event.KeeperState.SyncConnected){
            System.out.println("zookeeper connect success");
        }else if (watchedEvent.getState()== Event.KeeperState.Expired){
            //session超时
            try {
                System.out.println("zookeeper reconnecting...");
                ZooKeeper reconnectZooKeeper=new ZooKeeper(host, ZKConst.sessionTimeout,this);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
