package org.meizhuo.rpc.zksupport.service;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.meizhuo.rpc.client.RPCRequestNet;
import org.meizhuo.rpc.core.RPC;
import org.meizhuo.rpc.zksupport.ZKConst;
import org.meizhuo.rpc.zksupport.ZKTempZnodes;
import org.meizhuo.rpc.zksupport.watcher.ConsumerWatcher;
import org.meizhuo.rpc.zksupport.watcher.IPWatcher;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;
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
            zkTempZnodes.createTempSeqZnode(ZKConst.rootPath+ZKConst.servicePath+"/"+serviceName+ZKConst.consumersPath+ZKConst.consumerSeqNodePath,"1");
        }
    }

    public int getClientServiceNum(String serviceName) throws KeeperException, InterruptedException {
        ZKTempZnodes zkTempZnodes=new ZKTempZnodes(zooKeeper);
        IPWatcher ipWatcher=new IPWatcher(zooKeeper);
        List<String> children=zkTempZnodes.getPathChildren(ZKConst.rootPath+ZKConst.servicePath+"/"+serviceName+ZKConst.consumersPath,ipWatcher);
        return children.size();
    }

//    private List<String> getAllClients(String serviceName) throws KeeperException, InterruptedException {
//        ZKTempZnodes zkTempZnodes=new ZKTempZnodes(zooKeeper);
//        IPWatcher ipWatcher=new IPWatcher(zooKeeper);
//        return zkTempZnodes.getPathChildren(ZKConst.rootPath+ZKConst.servicePath+"/"+serviceName+ZKConst.consumersPath,ipWatcher);
//    }
//
//    //设置调用者数量并监听
//    public void watchAllClientService() throws KeeperException, InterruptedException {
//        Set<String> serviceSet=RPC.getClientConfig().getServiceInterface();
//        for (String serviceName:serviceSet){
//            List<String> ipList=getAllClients(serviceName);
//            ServiceInfo serviceInfo=new ServiceInfo();
//            serviceInfo.setClientCount(ipList.size());
//            RPCRequestNet.getInstance().serviceNameInfoMap.putIfAbsent(serviceName,serviceInfo);
//        }
//    }

    //获得并监听所有服务的信息 包括调用者和提供者
    public void getWatchAllServiceInfo() throws KeeperException, InterruptedException {
        Set<String> serviceSet=RPC.getClientConfig().getServiceInterface();
        for (String serviceName:serviceSet){
            System.out.println("MeiZhuoRPC get service:"+serviceName);
            ZKTempZnodes zkTempZnodes=new ZKTempZnodes(zooKeeper);
            IPWatcher providerWatcher=new IPWatcher(zooKeeper);
            List<String> providerIP=zkTempZnodes.getPathChildren(
                    ZKConst.rootPath+ZKConst.servicePath+"/"+serviceName+ZKConst.providersPath,
                    providerWatcher);
            ConsumerWatcher consumerWatcher=new ConsumerWatcher();
            List<String> consumerNodes=zkTempZnodes.getPathChildren(
                    ZKConst.rootPath+ZKConst.servicePath+"/"+serviceName+ZKConst.consumersPath,
                    consumerWatcher);
            ServiceInfo serviceInfo=new ServiceInfo();
            serviceInfo.setClientCount(providerIP.size());
            serviceInfo.setServerCount(consumerNodes.size());

        }
    }

    /**
     * 增加服务端某个服务IP的连接数
     * 能增加返回true 不能返回false
     * 检测到zk异常 例如版本乐观锁报错时 循环尝试
     * 直到数量正确或者无法继续添加
     * @param serviceName
     * @param IP
     * @param newNum 新的需要满足的连接数
     * @return
     * @throws InterruptedException
     * @throws UnsupportedEncodingException
     */
    public boolean addServiceServerConnectNum(String serviceName,String IP,int newNum) throws InterruptedException, UnsupportedEncodingException {
        String path=ZKConst.rootPath+ZKConst.balancePath+"/"+serviceName+"/"+IP;
        ZKTempZnodes zkTempZnodes=new ZKTempZnodes(zooKeeper);
        while (true){
            try {
                Stat stat=zkTempZnodes.exists(path);
                if (stat!=null){
                    int version=stat.getVersion();
                    String data = new String(zkTempZnodes.getData(path),
                            "UTF-8");
                    int oldData=Integer.valueOf(data);
                    if (oldData<newNum){
                        int newData=oldData+1;
                        zkTempZnodes.setData(path,(newData+"").getBytes(),version);
                        return true;
                    }
                    return false;
                }else {
                    zkTempZnodes.createTempSeqZnode(path, "1");
                    return true;
                }
            } catch (KeeperException e) {
                e.printStackTrace();
            }
        }
    }

}
