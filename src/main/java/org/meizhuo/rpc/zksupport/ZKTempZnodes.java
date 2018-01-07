package org.meizhuo.rpc.zksupport;

import org.apache.zookeeper.*;

import java.util.List;

/**
 * Created by wephone on 18-1-7.
 * zookeeper临时节点工具类
 */
public class ZKTempZnodes {

    private ZooKeeper zooKeeper;

    public ZKTempZnodes(ZooKeeper zooKeeper) {
        this.zooKeeper = zooKeeper;
    }

    //创建临时顺序节点
    public void createTempSeqZnode(String path,String data) throws KeeperException, InterruptedException {
        byte[] bytes=data.getBytes();
        zooKeeper.create(path,bytes, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
    }
    //获得某一服务的所有提供者ip
    public List<String> getPathChildren(String path, Watcher watcher) throws KeeperException, InterruptedException {
        List<String> children= zooKeeper.getChildren(path,watcher);
        return children;
    }
}
