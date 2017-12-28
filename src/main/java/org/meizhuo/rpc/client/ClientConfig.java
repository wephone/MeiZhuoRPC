package org.meizhuo.rpc.client;

import org.springframework.beans.BeansException;

/**
 * Created by wephone on 17-12-26.
 */
public class ClientConfig{

    public static String host;
    public static int port;
    //调用超时时间
    public static long overtime;

    private ClientConfig() {
    }

    /**
     * 加载Spring配置文件时，如果Spring配置文件中所定义的Bean类
     * 如果该类实现了ApplicationContextAware接口
     * 那么在加载Spring配置文件时，会自动调用ApplicationContextAware接口中的
     * @param applicationContext
     * @throws BeansException
     */
//    @Override
//    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
//        ClientConfig clientConfig=applicationContext.getBean(ClientConfig.class);
//
//    }
}
