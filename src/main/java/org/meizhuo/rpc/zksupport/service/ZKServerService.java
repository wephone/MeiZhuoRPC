package org.meizhuo.rpc.zksupport.service;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;
import org.meizhuo.rpc.core.RPC;
import org.meizhuo.rpc.zksupport.ZKConst;
import org.meizhuo.rpc.zksupport.ZKTempZnodes;

import java.util.Map;

/**
 * Created by wephone on 18-1-7.
 * 提供者端服务znode维护类
 */
public class ZKServerService {
    private ZooKeeper zooKeeper;

    public ZKServerService(ZooKeeper zooKeeper) {
        this.zooKeeper = zooKeeper;
    }

    //生成所有注册的服务znode
    public void createServerService() throws KeeperException, InterruptedException {
        ZKTempZnodes zkTempZnodes=new ZKTempZnodes(zooKeeper);
        Map<String,String> serviceMap= RPC.getServerConfig().getServerImplMap();
        String ip=RPC.getServerConfig().getServerHost();
        for (Map.Entry<String,String> entry:serviceMap.entrySet()){
            //获取配置中设置的IP设置为IP顺序节点的值 默认127.0.0.1:8888
            zkTempZnodes.createTempSeqZnode(ZKConst.rootPath+ZKConst.servicePath+"/"+entry.getKey()+ZKConst.consumersPath+ZKConst.consumerSeqNodePath,ip);
        }
    }

}
