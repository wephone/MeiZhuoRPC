package org.meizhuo.rpc.client;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;
import org.meizhuo.rpc.core.RPC;
import org.meizhuo.rpc.zksupport.LoadBalance.LoadBalance;
import org.meizhuo.rpc.zksupport.LoadBalance.MinConnectRandom;
import org.meizhuo.rpc.zksupport.ZKConnect;
import org.meizhuo.rpc.zksupport.service.ZKClientService;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.io.IOException;
import java.util.Set;

/**
 * Created by wephone on 17-12-26.
 */
public class ClientConfig implements ApplicationContextAware {

//    private String host;
//    private int port;
    //zookeeper集群地址 逗号隔开
    private String zooKeeperHost;
    //调用超时时间
    private long overtime;
    //远程调用接口全类名集合 用于启动时向zookeeper注册提供者服务
    private Set<String> serviceInterface;
    private LoadBalance loadBalance;

    public String getZooKeeperHost() {
        return zooKeeperHost;
    }

    public void setZooKeeperHost(String zooKeeperHost) {
        zooKeeperHost = zooKeeperHost;
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
        //获得IOC容器后 读取配置中的服务 向zookeeper注册znode
        try {
            ZooKeeper zooKeeper= new ZKConnect().clientConnect();
            ZKClientService zkClientService=new ZKClientService(zooKeeper);
            zkClientService.createClientService();
            //获取提供者调用者ip及数量 并监听 即对所有服务开启平衡
            //负载均衡类设置prototype作用域
            LoadBalance loadBalance=RPC.getClientConfig().getLoadBalance();
            loadBalance.balanceAll(zooKeeper);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (KeeperException e) {
            e.printStackTrace();
        }
    }
}
