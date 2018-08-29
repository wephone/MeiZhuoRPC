package org.meizhuo.rpc.server;

import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.MessageToMessageEncoder;
import org.meizhuo.rpc.core.RPC;
import org.meizhuo.rpc.protocol.*;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.List;
import java.util.Map;

/**
 * Created by wephone on 17-12-26.
 */
public class ServerConfig implements ApplicationContextAware{

    private int port=8888;
    //zookeeper集群地址 逗号隔开
    private String zooKeeperHost;
    //服务提供者IP 没配置默认127.0.0.1 8888端口
    private String serverHost="127.0.0.1";
    /**
     * key为serviceId标识 value为对应的实现类bean
     * 实现类可以不显式实现抽象接口 但必须要有对应的实现方法
     */
    private Map<String,Object> serverImplMap;
    private ProtocolEnum protocol=ProtocolEnum.MeiZhuoJavaProtocol;

    public LengthFieldBasedFrameDecoder getDecoder(){
        switch (protocol){
            case MeiZhuoGeneralProtocol:
                return null;
            default:
                //默认协议
                //最大包长1024平方 长度位置偏移0字节 大小一个int
                return new JavaRPCDecoder(1024*1024,0,4);
        }
    }

    public MessageToMessageEncoder getEncoder(){
        switch (protocol){
            case MeiZhuoGeneralProtocol:
                return null;
            default:
                //默认协议
                return new JavaRPCEncoder();
        }
    }

    public RPCProtocol getRPCProtocol(){
        switch (protocol){
            case MeiZhuoGeneralProtocol:
                return null;
            default:
                //默认协议
                return new MZJavaProtocol();
        }
    }

    public int getPort() {
        return port;
    }

    public Map<String, Object> getServerImplMap() {
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
        this.zooKeeperHost = zooKeeperHost;
    }

    public String getServerHost() {
        //获取时带上端口
        return serverHost+":"+port;
    }

    public void setServerHost(String serverHost) {
        this.serverHost = serverHost;
    }

    public void setServerImplMap(Map<String, Object> serverImplMap) {
        this.serverImplMap = serverImplMap;
    }

    public ProtocolEnum getProtocol() {
        return protocol;
    }

    public void setProtocol(ProtocolEnum protocol) {
        this.protocol = protocol;
    }

    //运行过程中获取IOC容器
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        RPC.serverContext=applicationContext;
        if (RPC.isTrace()){
            RuntimeMXBean runtimeMBean = ManagementFactory.getRuntimeMXBean();
            List<String> vmArgs=runtimeMBean.getInputArguments();
            for (String arg:vmArgs){
                if (arg.contains("transmittable-thread-local")){
                    RPC.getTraceConfig().setEnableTrace(true);
                    break;
                }
            }
        }
    }
}
