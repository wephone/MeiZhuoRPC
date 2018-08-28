package org.meizhuo.rpc.zksupport.service;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;
import org.meizhuo.rpc.core.RPC;
import org.meizhuo.rpc.zksupport.ZKConst;
import org.meizhuo.rpc.zksupport.ZKTempZnodes;
import org.meizhuo.rpc.zksupport.watcher.IPWatcher;

import java.util.List;
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
        Map<String,Object> serviceMap= RPC.getServerConfig().getServerImplMap();
        String ip=RPC.getServerConfig().getServerHost();
        for (Map.Entry<String,Object> entry:serviceMap.entrySet()){
            //获取配置中设置的IP设置为IP顺序节点的值 默认127.0.0.1:8888
            zkTempZnodes.createTempZnode(ZKConst.rootPath+ZKConst.servicePath+"/"+entry.getKey()+ZKConst.providersPath+"/"+ip,null);
            //创建连接数节点 首次增加时连接数为0
//            zkTempZnodes.createTempZnode(ZKConst.rootPath+ZKConst.balancePath+"/"+entry.getKey()+"/"+ip,0+"");
        }
    }

    //获得这个服务所有的提供者 包含监听注册
    public List<String> getAllServiceIP(String serviceName) throws KeeperException, InterruptedException {
        ZKTempZnodes zkTempZnodes=new ZKTempZnodes(zooKeeper);
        IPWatcher ipWatcher=new IPWatcher(zooKeeper);
        return zkTempZnodes.getPathChildren(ZKConst.rootPath+ZKConst.servicePath+"/"+serviceName+ZKConst.providersPath,ipWatcher);
    }

    //初始化根节点及服务提供者节点 均为持久节点
    public void initZnode() throws KeeperException, InterruptedException {
        ZKTempZnodes zkTempZnodes=new ZKTempZnodes(zooKeeper);
        StringBuilder pathBuilder=new StringBuilder(ZKConst.rootPath);
//        String balancePath=ZKConst.rootPath;
        zkTempZnodes.createSimpleZnode(pathBuilder.toString(),null);
//        balancePath=balancePath+ZKConst.balancePath;
//        zkTempZnodes.createSimpleZnode(balancePath,null);
        pathBuilder.append(ZKConst.servicePath);
        zkTempZnodes.createSimpleZnode(pathBuilder.toString(),null);
        Map<String,Object> serverImplMap=RPC.getServerConfig().getServerImplMap();
        for (Map.Entry<String,Object> entry:serverImplMap.entrySet()){
//            zkTempZnodes.createSimpleZnode(balancePath+"/"+entry.getKey(),null);
            StringBuilder serviceBuilder=new StringBuilder(pathBuilder.toString());
            serviceBuilder.append("/");
            serviceBuilder.append(entry.getKey());
            zkTempZnodes.createSimpleZnode(serviceBuilder.toString(),null);
            serviceBuilder.append(ZKConst.providersPath);
            zkTempZnodes.createSimpleZnode(serviceBuilder.toString(),null);
        }
    }


}
