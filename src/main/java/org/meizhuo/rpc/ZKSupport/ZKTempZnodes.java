package org.meizhuo.rpc.ZKSupport;

import org.apache.zookeeper.ZooKeeper;

import java.util.List;

/**
 * Created by wephone on 18-1-7.
 * zookeeper临时节点工具类
 */
public class ZKTempZnodes {

    private ZooKeeper zooKeeper;
    private String path;

    public ZKTempZnodes(ZooKeeper zooKeeper, String path) {
        this.zooKeeper = zooKeeper;
        this.path = path;
    }

    //创建临时顺序节点
    public void createIPZnode(){

    }
    //获得某一服务的所有提供者ip
    public List<String> getServicesIPList(){
        return null;
    }
}
