package org.meizhuo.rpc.zksupport.service;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.meizhuo.rpc.Exception.ProvidersNoFoundException;
import org.meizhuo.rpc.client.IPChannelInfo;
import org.meizhuo.rpc.client.RPCRequest;
import org.meizhuo.rpc.client.RPCRequestNet;
import org.meizhuo.rpc.core.RPC;
import org.meizhuo.rpc.zksupport.LoadBalance.BalanceThreadPool;
import org.meizhuo.rpc.zksupport.LoadBalance.ReleaseChannelRunnable;
import org.meizhuo.rpc.zksupport.ZKConst;
import org.meizhuo.rpc.zksupport.ZKTempZnodes;
import org.meizhuo.rpc.zksupport.watcher.ConsumerWatcher;
import org.meizhuo.rpc.zksupport.watcher.IPWatcher;

import java.io.UnsupportedEncodingException;
import java.nio.file.ProviderNotFoundException;
import java.util.*;

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
            //创建消费者服务节点 不放数据
            zkTempZnodes.createTempSeqZnode(ZKConst.rootPath+ZKConst.servicePath+"/"+serviceName+ZKConst.consumersPath+ZKConst.consumerSeqNodePath,null);
        }
    }

    //获得这个服务所有的客户端 包含监听的注册
    public List<String> getServiceClients(String serviceName) throws KeeperException, InterruptedException {
        ZKTempZnodes zkTempZnodes=new ZKTempZnodes(zooKeeper);
        ConsumerWatcher consumerWatcher=new ConsumerWatcher(zooKeeper);
        List<String> children=zkTempZnodes.getPathChildren(ZKConst.rootPath+ZKConst.servicePath+"/"+serviceName+ZKConst.consumersPath,consumerWatcher);
        return children;
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
//    public void getWatchAllServiceInfo() throws KeeperException, InterruptedException {
//        Set<String> serviceSet=RPC.getClientConfig().getServiceInterface();
//        for (String serviceName:serviceSet){
//            System.out.println("MeiZhuoRPC get service:"+serviceName);
//            ZKTempZnodes zkTempZnodes=new ZKTempZnodes(zooKeeper);
//            IPWatcher providerWatcher=new IPWatcher(zooKeeper);
//            List<String> providerIP=zkTempZnodes.getPathChildren(
//                    ZKConst.rootPath+ZKConst.servicePath+"/"+serviceName+ZKConst.providersPath,
//                    providerWatcher);
//            ConsumerWatcher consumerWatcher=new ConsumerWatcher();
//            List<String> consumerNodes=zkTempZnodes.getPathChildren(
//                    ZKConst.rootPath+ZKConst.servicePath+"/"+serviceName+ZKConst.consumersPath,
//                    consumerWatcher);
//            ServiceInfo serviceInfo=new ServiceInfo();
//            serviceInfo.setClientCount(providerIP.size());
//            serviceInfo.setServerCount(consumerNodes.size());
//
//        }
//    }

    //初始化根节点及服务消费者者节点 均为持久节点
    public void initZnode() throws KeeperException, InterruptedException {
        ZKTempZnodes zkTempZnodes=new ZKTempZnodes(zooKeeper);
        String path=ZKConst.rootPath;
        zkTempZnodes.createSimpleZnode(path,null);
        path=path+ZKConst.servicePath;
        zkTempZnodes.createSimpleZnode(path,null);
        Set<String> set=RPC.getClientConfig().getServiceInterface();
        for (String service:set){
            zkTempZnodes.createSimpleZnode(path+"/"+service,null);
            zkTempZnodes.createSimpleZnode(path+"/"+service+ZKConst.consumersPath,null);
        }
    }

    /**
     * 增加服务端某个服务IP的连接数
     * 检测到zk异常 例如版本乐观锁报错时 循环尝试
     * 成功选出连接数最小的一个 并将连接数+1
     * 直到set大小满足新连接数时退出
     * 只要watcher机制正常 通知到的最后一次平衡操作一定得到正确的连接
     * @param serviceName
     * @param newNum 新的需要满足的连接数
     * @return 返回新加入了连接成功的IP集合
     * @throws InterruptedException
     * @throws UnsupportedEncodingException
     */
    public Set<String> addServiceServerConnectNum(String serviceName,Set<String> oldIPSet,int newNum) throws InterruptedException, UnsupportedEncodingException, KeeperException {
        Set<String> newIPSet=oldIPSet;
        String path=ZKConst.rootPath+ZKConst.balancePath+"/"+serviceName;
        ZKTempZnodes zkTempZnodes=new ZKTempZnodes(zooKeeper);
        //不做监听 用乐观锁
        List<String> allIP=zkTempZnodes.getPathChildren(path,null);
        allIP.removeAll(oldIPSet);//已有的不再重复再连接
        //重复循环 给乐观锁重试的机会
        while (true){
            if (allIP.size()==0){
                //没有可用的服务端节点了则退出
                return newIPSet;
            }
            if (newIPSet.size()==newNum){
                //够数了就返回
                return newIPSet;
            }
            //选出最小的一个连接数及其ip
            int minConnectNum=0;
            String minIP="";
            int minVersion=0;//zk的数据版本是从0开始计数的
            int minIndex=0;
            for (int i = 0; i <allIP.size() ; i++) {
                String ip=allIP.get(i);
                String ipPath=path+"/"+ip;
                Stat stat=zkTempZnodes.exists(ipPath);
                String data = new String(zkTempZnodes.getData(ipPath),
                        "UTF-8");
                Integer oldConnectNum=Integer.valueOf(data);
                if ((oldConnectNum<minConnectNum)||i==0){
                    //比对 筛选出最小连接的节点  第一个尝试的IP当做最小的
                    minConnectNum=oldConnectNum;
                    minIP=ip;
                    minVersion=stat.getVersion();
                    minIndex=i;
                }
            }
            try {
                String minPath=path+"/"+minIP;
                int newData=minConnectNum+1;
                zkTempZnodes.setData(minPath,(newData+"").getBytes(),minVersion);
                //设置成功后在连接成功集合中添加 并在待选的ip中去除
                newIPSet.add(minIP);
                //不存在则赋初值
                RPCRequestNet.getInstance().IPChannelMap.putIfAbsent(minIP,new IPChannelInfo());
                //增加服务引用次数
                RPCRequestNet.getInstance().IPChannelMap.get(minIP).incrementServiceQuoteNum();
                allIP.remove(minIndex);
            } catch (KeeperException.BadVersionException e) {
                //乐观锁报错 重新循环尝试
                e.printStackTrace();
            }
        }
    }

    /**
     * 减少服务端某个服务IP的连接数
     * 检测到zk异常 例如版本乐观锁报错时 循环尝试
     * 成功选出连接数最大的一个 并将连接数-1
     * 直到set大小满足新连接数时退出
     * 只要watcher机制正常 通知到的最后一次平衡操作一定得到正确的连接
     * @param serviceName
     * @param newNum 新的需要满足的连接数
     * @return 返回减持连接的IP集合
     * @throws InterruptedException
     * @throws UnsupportedEncodingException
     */
    public Set<String> reduceServiceServerConnectNum(String serviceName,Set<String> oldIPSet,int newNum) throws KeeperException, InterruptedException, UnsupportedEncodingException {
        String path=ZKConst.rootPath+ZKConst.balancePath+"/"+serviceName;
        ZKTempZnodes zkTempZnodes=new ZKTempZnodes(zooKeeper);
        for (String oldIP:oldIPSet){
            if (zkTempZnodes.exists(path+"/"+oldIP)==null){
                //该IP已经不存在 则不应该将他加入计算最小连接数 直接去除
                oldIPSet.remove(oldIP);
            }
        }
        Set<String> newIPSet=oldIPSet;
        List<String> availList=new ArrayList<>();
        availList.addAll(oldIPSet);//当前可选择的IP(已连接过,本次平衡未减连接的)
        //乐观锁给重试机会
        while (true){
            if (newIPSet.size()==newNum){
                return newIPSet;
            }
            int maxConnectNum=1;//至少连接了一个
            String maxIP="";
            int maxVersion=0;
            int maxIndex=0;
            for (int i = 0; i <availList.size() ; i++) {
                String ipPath=path+"/"+availList.get(i);
                Stat stat=zkTempZnodes.exists(ipPath);
                if (stat!=null){
                    String data = new String(zkTempZnodes.getData(ipPath),
                            "UTF-8");
                    Integer oldConnectNum=Integer.valueOf(data);
                    if ((oldConnectNum>maxConnectNum)||i==0){
                        maxConnectNum=oldConnectNum;
                        maxIP=availList.get(i);
                        maxVersion=stat.getVersion();
                        maxIndex=i;
                    }
                }else {
                    //说明节点不存在 直接去除
                    String abandonIP=availList.get(i);
                    availList.remove(i);
                    //减少服务引用次数
                    int remain=RPCRequestNet.getInstance().IPChannelMap.get(abandonIP).decrementServiceQuoteNum();
                    if (remain==0){
                        //加入线程池中 释放资源 关闭通道
                        BalanceThreadPool.execute(new ReleaseChannelRunnable(maxIP));
                    }
                    newIPSet.remove(abandonIP);
                }
            }
            try {
                //这里对没有可用提供者不抛出异常 直接返回一个空set 等待选择IP时抛出异常
                if (!maxIP.equals("")){
                    String maxPath = path + "/" + maxIP;
                    int newData = maxConnectNum - 1;
                    zkTempZnodes.setData(maxPath, (newData + "").getBytes(), maxVersion);
                    newIPSet.remove(maxIP);
                    //减少服务引用次数 TODO 剩余引用为0时加入线程池 等待超时时间后 再判断一次 若仍无引用 关闭此通道释放网络连接
                    int remain = RPCRequestNet.getInstance().IPChannelMap.get(maxIP).decrementServiceQuoteNum();
                    if (remain == 0) {
                        //加入线程池中 释放资源 关闭通道
                        BalanceThreadPool.execute(new ReleaseChannelRunnable(maxIP));
                    }
                    availList.remove(maxIndex);
                }else {
                    //TODO 输出warn日志
                    System.out.println(serviceName+"服务已无可用提供者");
                    return new HashSet<>();
                }
            } catch (KeeperException.BadVersionException e) {
                //乐观锁报错 重新循环尝试
                e.printStackTrace();
            }
        }
    }

}
