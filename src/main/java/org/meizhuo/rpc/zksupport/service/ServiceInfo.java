package org.meizhuo.rpc.zksupport.service;

import org.meizhuo.rpc.client.IPChannelInfo;
import org.meizhuo.rpc.client.RPCRequestNet;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by wephone on 18-1-8.
 * 每个服务对应的信息存放类
 * 用在一个key为服务名字的serviceNameInfoMap里
 */
public class ServiceInfo {

    //这个服务的提供者和调用者的数量
    private AtomicInteger clientCount=new AtomicInteger(0);//原子变量都要赋初值
    private AtomicInteger serverCount=new AtomicInteger(0);
    //这个服务所连接的提供者IP Set 只能由负载均衡类操作 ConcurrentSkipListSet线程安全的有序集合
    private ConcurrentSkipListSet<String> serviceIPSet=new ConcurrentSkipListSet<>();

    public int getClientCount() {
        return clientCount.get();
    }

    public void setClientCount(int clientCount) {
        this.clientCount.set(clientCount);
    }

    public int getServerCount() {
        return serverCount.get();
    }

    public void setServerCount(int serverCount) {
        this.serverCount.set(serverCount);
    }

    public ConcurrentSkipListSet<String> getConnectIPSet() {
        return serviceIPSet;
    }

    public void setServiceIPSet(Set<String> serviceIPSet) {
        this.serviceIPSet.clear();
        this.serviceIPSet.addAll(serviceIPSet);
    }

    public int getConnectIPSetCount(){
        return serviceIPSet.size();
    }

    public void addConnectIP(String IP) {
        serviceIPSet.add(IP);
    }

    public void removeConnectIP(String IP){
        serviceIPSet.remove(IP);
    }
}
