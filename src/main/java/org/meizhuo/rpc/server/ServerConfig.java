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

    public void setServerImplMap(Map<String, String> serverImplMap) {
        this.serverImplMap = serverImplMap;
    }

    //获取运行过程中的IOC容器
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        RPC.serverContext=applicationContext;
    }
}
