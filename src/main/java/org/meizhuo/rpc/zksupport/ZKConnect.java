package org.meizhuo.rpc.zksupport;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.meizhuo.rpc.core.RPC;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 * Created by wephone on 18-1-7.
 */
public class ZKConnect {

    private ZooKeeper zooKeeper;

    private ZooKeeper connect(String host) throws InterruptedException, IOException {
        CountDownLatch countDownLatch=new CountDownLatch(1);
        //2秒sessionTimeOut
        zooKeeper=new ZooKeeper(host, ZKConst.sessionTimeout, new Watcher() {
            @Override
            public void process(WatchedEvent watchedEvent) {
                if (watchedEvent.getState()== Event.KeeperState.SyncConnected){
                    countDownLatch.countDown();
                }
            }
        });
        countDownLatch.await();
        return zooKeeper;
    }

    //TODO 后续支持zookeeper集群模式
    public ZooKeeper serverConnect() throws IOException, InterruptedException {
        String host=RPC.getServerConfig().getZooKeeperHost();
        return this.connect(host);
    }

    public ZooKeeper clientConnect() throws IOException, InterruptedException {
        String host=RPC.getClientConfig().getZooKeeperHost();
        return this.connect(host);
    }

}
