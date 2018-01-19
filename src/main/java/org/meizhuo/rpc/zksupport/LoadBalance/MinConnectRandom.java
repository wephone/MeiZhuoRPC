package org.meizhuo.rpc.zksupport.LoadBalance;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;
import org.meizhuo.rpc.Exception.ProvidersNoFoundException;
import org.meizhuo.rpc.client.RPCRequestNet;
import org.meizhuo.rpc.core.RPC;
import org.meizhuo.rpc.zksupport.service.ServiceInfo;
import org.meizhuo.rpc.zksupport.service.ZKClientService;
import org.meizhuo.rpc.zksupport.service.ZKServerService;
import org.meizhuo.rpc.zksupport.service.ZnodeType;

import java.io.UnsupportedEncodingException;
import java.nio.file.ProviderNotFoundException;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by wephone on 18-1-7.
 * 负载均衡策略操作类 保证每个提供者都被连接使用到
 * 负载均衡类非单例
 * 采用最小连接数平衡连接 再随机选择每个服务已分配的IP
 */
@Deprecated
public class MinConnectRandom{

//    @Override
    public void balanceAll(ZooKeeper zookeeper) {
        Set<String> allServices= RPC.getClientConfig().getServiceInterface();
        ZKClientService zkClientService=new ZKClientService(zookeeper);
        ZKServerService zkServerService=new ZKServerService(zookeeper);
        try {
            for (String service : allServices) {
                ReadWriteLock readWriteLock=new ReentrantReadWriteLock();
                //不存在则新建一个锁键值对 存在则不操作
//                BalanceThreadPool.serviceLockMap.putIfAbsent(service,readWriteLock);
//                //上写锁
//                System.out.println(service+"正在平衡...已加写锁");
//                BalanceThreadPool.serviceLockMap.get(service).writeLock().lock();
//                List<String> clientZnodes = zkClientService.getServiceClients(service);
                List<String> serverZnodes = zkServerService.getAllServiceIP(service);
                RPCRequestNet.getInstance().serviceNameInfoMap.putIfAbsent(service,new ServiceInfo());
                ServiceInfo serviceInfo=RPCRequestNet.getInstance().serviceNameInfoMap.get(service);
//                serviceInfo.setClientCount(clientZnodes.size());
//                serviceInfo.setServerCount(serverZnodes.size());
//                int newConnectNum=getConnectNum(clientZnodes.size(),serverZnodes.size());
                Set<String> newIPSet= new HashSet<>();
                //启动时增加连接服务端
//                try {
//                    newIPSet = zkClientService.addServiceServerConnectNum(service,serviceInfo.getConnectIPSet(),newConnectNum);
//                } catch (UnsupportedEncodingException e) {
//                    e.printStackTrace();
//                }
//                serviceInfo.setServiceIPSet(newIPSet);
                //写入新serviceInfo
                RPCRequestNet.getInstance().serviceNameInfoMap.put(service,serviceInfo);
                //释放写锁
//                BalanceThreadPool.serviceLockMap.get(service).writeLock().unlock();
                System.out.println(service+"已平衡 释放写锁,持有连接:"+serviceInfo.getConnectIPSetCount());
            }
            //下面这句出现了好像死锁
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (KeeperException e) {
            e.printStackTrace();
        }
    }

//    @Override
    public void balance(ZooKeeper zooKeeper, String serviceName, List<String> znodes, ZnodeType type) {
        final String service=serviceName;
        Runnable runnable=new Runnable() {
            @Override
            public void run() {
//                ReadWriteLock readWriteLock=new ReentrantReadWriteLock();
                //不存在则新建一个锁键值对 存在则不操作 第一次平衡全部时已赋值锁
//                BalanceThreadPool.serviceLockMap.putIfAbsent(service,readWriteLock);
                //上写锁
//                BalanceThreadPool.serviceLockMap.get(service).writeLock().lock();
                System.out.println(serviceName+"正在平衡...已加写锁");
                //原先不存在则新建一个
                RPCRequestNet.getInstance().serviceNameInfoMap.putIfAbsent(service,new ServiceInfo());
                ServiceInfo serviceInfo=RPCRequestNet.getInstance().serviceNameInfoMap.get(service);
                int oldConnectNum=serviceInfo.getConnectIPSetCount();
                if (type==ZnodeType.consumer){
//                    serviceInfo.setClientCount(znodes.size());
                }else {
//                    serviceInfo.setServerCount(znodes.size());
                }
//                int newConnectNum=getConnectNum(serviceInfo.getClientCount(),serviceInfo.getServerCount());
                ZKClientService zkClientService=new ZKClientService(zooKeeper);
//                try {
                    //需要新增连接时采用最小连接数策略 连接该服务连接数最少的一个节点
//                    if (newConnectNum>oldConnectNum){
//                        Set<String> newIPSet=zkClientService.addServiceServerConnectNum(serviceName,serviceInfo.getConnectIPSet(),newConnectNum);
//                        serviceInfo.setServiceIPSet(newIPSet);
//                    }else {
//                        //否则减少对服务端的连接
//                        Set<String> newIPSet=zkClientService.reduceServiceServerConnectNum(serviceName,serviceInfo.getConnectIPSet(),newConnectNum);
//                        serviceInfo.setServiceIPSet(newIPSet);
//                    }
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                } catch (UnsupportedEncodingException e) {
//                    e.printStackTrace();
//                } catch (KeeperException e) {
//                    e.printStackTrace();
//                }
                //写入新serviceInfo
                RPCRequestNet.getInstance().serviceNameInfoMap.put(service,serviceInfo);
                //释放写锁
//                BalanceThreadPool.serviceLockMap.get(service).writeLock().unlock();
                System.out.println(serviceName+"平衡结束 持有连接数:"+serviceInfo.getConnectIPSetCount());
            }
        };
        BalanceThreadPool.execute(runnable);
    }

//    @Override
    public String chooseIP(String serviceName) throws ProvidersNoFoundException {
//        if (BalanceThreadPool.serviceLockMap.get(serviceName)==null){
//            System.out.println(serviceName+"服务未初始化");
//            throw new ProvidersNoFoundException();
//        }
//        //获取serviceInfo上读锁
//        BalanceThreadPool.serviceLockMap.get(serviceName).readLock().lock();
//        System.out.println(serviceName+"正在选择IP...已加读锁");
//        ConcurrentSkipListSet<String> IPSet=RPCRequestNet.getInstance().serviceNameInfoMap.get(serviceName)
//                .getConnectIPSet();
        //释放读锁
//        BalanceThreadPool.serviceLockMap.get(serviceName).readLock().unlock();
//        System.out.println(serviceName+"选择IP完毕 释放读锁");
//        int num=IPSet.size();
//        if (num==0){
//            throw new ProvidersNoFoundException();
//        }else {
//            //随机返回一个
//            Random random = new Random();
//            //生成[0,num)区间的整数：
//            int index = random.nextInt(num);
//            int count = 0;
//            for (String ip : IPSet) {
//                if (count == index) {
//                    //返回随机生成的索引位置ip
//                    return ip;
//                }
//                count++;
//            }
//            return IPSet.first();
//        }
        return null;
    }

    private int getConnectNum(int clientNum,int serverNum){
        int connectNum=0;
        if (serverNum<=clientNum){
            connectNum=1;
        }else {
            connectNum=serverNum/clientNum;
            if ((serverNum%clientNum)!=0){
                connectNum++;
            }
        }
        return connectNum;
    }



}
