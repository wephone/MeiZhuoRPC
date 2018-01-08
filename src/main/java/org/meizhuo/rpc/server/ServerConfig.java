package org.meizhuo.rpc.server;

import org.meizhuo.rpc.core.RPC;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.Map;

/**
 * Created by wephone on 17-12-26.
 */
public class ServerConfig implements ApplicationContextAware{

    private int port;
    //zookeeper集群地址 逗号隔开
    private String zooKeeperHost;
    //服务提供者IP 没配置默认127.0.0.1 8888端口
    private String serverHost;

    private Map<String,String> serverImplMap;

    public int getPort() {
        return port;
    }

    public Map<String, String> getServerImplMap() {
        return serverImplMap;
    }

    //为spring提供setter
    public void setPort(int port) {
        this.port = port;
    }

    public String getZooKeeperHost() {
        return zooKeeperHost;
    }

    public void setZooKeeperHost(String zooKeeperHost) {
        zooKeeperHost = zooKeeperHost;
    }

    public String getServerHost() {
        if (serverHost==null){
            serverHost="127.0.0.1:8888";
        }
        return serverHost;
    }

    public void setServerHost(String serverHost) {
        this.serverHost = serverHost;
    }

    public void setServerImplMap(Map<String, String> serverImplMap) {
        this.serverImplMap = serverImplMap;
    }

    //运行过程中获取IOC容器
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        RPC.serverContext=applicationContext;
    }
}
