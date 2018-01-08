package org.meizhuo.rpc.zksupport.service;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;
import org.meizhuo.rpc.core.RPC;
import org.meizhuo.rpc.zksupport.ZKConst;
import org.meizhuo.rpc.zksupport.ZKTempZnodes;

import java.util.Set;

/**
 * Created by wephone on 18-1-8.
 */
public class ZKClientService {

    private ZooKeeper zooKeeper;

    public ZKClientService(ZooKeeper zooKeeper) {
        this.zooKeeper = zooKeeper;
    }

    //注册消费者需要的服务znode
    public void createClientService() throws KeeperException, InterruptedException {
        ZKTempZnodes zkTempZnodes=new ZKTempZnodes(zooKeeper);
        Set<String> services= RPC.getClientConfig().getServiceInterface();
        for (String serviceName:services){
            //创建消费者服务节点 单纯放个1作为数据就行了
            zkTempZnodes.createTempSeqZnode(ZKConst.rootPath+ZKConst.servicePath+"/"+serviceName+ZKConst.providersPath+ZKConst.ipSeqPath,"1");
        }
    }

}
