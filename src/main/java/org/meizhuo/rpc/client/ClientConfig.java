package org.meizhuo.rpc.client;

import org.meizhuo.rpc.core.RPC;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * Created by wephone on 17-12-26.
 */
public class ClientConfig implements ApplicationContextAware {

    private String host;
    private int port;
    //调用超时时间
    private long overtime;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public long getOvertime() {
        return overtime;
    }

    public void setOvertime(long overtime) {
        this.overtime = overtime;
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
    }
}
