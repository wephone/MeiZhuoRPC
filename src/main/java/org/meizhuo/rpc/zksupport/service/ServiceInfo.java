package org.meizhuo.rpc.zksupport.service;

import org.meizhuo.rpc.client.IPChannelInfo;
import org.meizhuo.rpc.client.RPCRequestNet;

import java.util.HashSet;
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

    //用于轮询负载均衡策略
    private AtomicInteger index=new AtomicInteger(0);
    //这个服务所连接的提供者IP Set 只能由负载均衡类操作
    private Set<String> serviceIPSet=new HashSet<>();


//    public void setServiceIPSet(Set<String> serviceIPSet) {
    public void setServiceIPSet(List<String> newIPSet) {
        Set<String> set=new HashSet<>();
        set.addAll(newIPSet);
        this.serviceIPSet.clear();
        this.serviceIPSet.addAll(set);
    }

    public Set<String> getServiceIPSet() {
        return serviceIPSet;
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
