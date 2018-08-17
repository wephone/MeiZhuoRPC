package org.meizhuo.rpc.client;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;
import org.meizhuo.rpc.core.RPC;
import org.meizhuo.rpc.zksupport.LoadBalance.LoadBalance;
import org.meizhuo.rpc.zksupport.ZKConnect;
import org.meizhuo.rpc.zksupport.service.ServiceInfo;
import org.meizhuo.rpc.zksupport.service.ZKServerService;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by wephone on 17-12-26.
 */
public class ClientConfig implements ApplicationContextAware {

//    private String host;
//    private int port;
    //zookeeper集群地址 逗号隔开
    private String zooKeeperHost;
    //调用超时时间 默认3秒
    private long overtime=3000;
    //远程调用接口全类名集合 用于启动时向zookeeper注册提供者服务
    private Set<String> serviceInterface;
    private LoadBalance loadBalance;
    private Integer poolMaxIdle=2;
    private Integer poolMaxTotal=4;

    public String getZooKeeperHost() {
        return zooKeeperHost;
    }

    public void setZooKeeperHost(String zooKeeperHost) {
        this.zooKeeperHost = zooKeeperHost;
    }

    public long getOvertime() {
        return overtime;
    }

    public void setOvertime(long overtime) {
        this.overtime = overtime;
    }

    public Set getServiceInterface() {
        return serviceInterface;
    }

    public void setServiceInterface(Set serviceInterface) {
        this.serviceInterface = serviceInterface;
    }

    public LoadBalance getLoadBalance() {
        return loadBalance;
    }

    public void setLoadBalance(LoadBalance loadBalance) {
        this.loadBalance = loadBalance;
    }

    public Integer getPoolMaxIdle() {
        return poolMaxIdle;
    }

    public void setPoolMaxIdle(Integer poolMaxIdle) {
        this.poolMaxIdle = poolMaxIdle;
    }

    public Integer getPoolMaxTotal() {
        return poolMaxTotal;
    }

    public void setPoolMaxTotal(Integer poolMaxTotal) {
        this.poolMaxTotal = poolMaxTotal;
    }

    /**
     * 加载Spring配置文件时，如果Spring配置文件中所定义的Bean类
     * 如果该类实现了ApplicationContextAware接口
     * 那么在加载Spring配置文件时，会自动调用ApplicationContextAware接口中的
     * @param applicationContext
     * @throws BeansException
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        RPC.clientContext=applicationContext;
        //获得IOC容器后 读取配置中的服务
        try {
            ZooKeeper zooKeeper= new ZKConnect().clientConnect();
            ZKServerService zkServerService=new ZKServerService(zooKeeper);
            Set<String> services=RPC.getClientConfig().getServiceInterface();
            //初始化所有可用IP 初始化读写锁
            for (String service:services){
                List<String> ips=zkServerService.getAllServiceIP(service);
                for (String ip:ips){
                    RPCRequestNet.getInstance().IPChannelMap.putIfAbsent(ip,new IPChannelInfo());
                }
                ServiceInfo serviceInfo=new ServiceInfo();
                serviceInfo.setServiceIPSet(ips);
                ReadWriteLock readWriteLock=new ReentrantReadWriteLock();
                RPCRequestNet.getInstance().serviceLockMap.putIfAbsent(service,readWriteLock);
                RPCRequestNet.getInstance().serviceNameInfoMap.putIfAbsent(service,serviceInfo);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (KeeperException e) {
            e.printStackTrace();
        }
    }
}
