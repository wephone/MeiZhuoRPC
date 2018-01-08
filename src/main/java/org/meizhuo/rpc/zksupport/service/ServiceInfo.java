package org.meizhuo.rpc.zksupport.service;

import org.meizhuo.rpc.client.IPChannelInfo;
import org.meizhuo.rpc.client.RPCRequestNet;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by wephone on 18-1-8.
 * 每个服务对应的信息存放类
 * 用在一个key为服务名字的serviceNameInfoMap里
 */
public class ServiceInfo {

    //这个服务的提供者和调用者的数量
    private AtomicInteger clientCount;
    private AtomicInteger serverCount;
    //这个服务所连接的提供者IP List
    private CopyOnWriteArrayList<String> serviceIPList;

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

    public List<String> getConnectIPList() {
        return serviceIPList;
    }

    public void addConnectIP(String IP) {
        serviceIPList.add(IP);
    }


}
