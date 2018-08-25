package org.meizhuo.rpc.server;

import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.MessageToMessageEncoder;
import org.meizhuo.rpc.core.RPC;
import org.meizhuo.rpc.protocol.JavaRPCDecoder;
import org.meizhuo.rpc.protocol.JavaRPCEncoder;
import org.meizhuo.rpc.protocol.MZJavaProtocol;
import org.meizhuo.rpc.protocol.RPCProtocol;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

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

    private Map<String,String> serverImplMap;

    public LengthFieldBasedFrameDecoder getDecoder(){
        //TODO 后续根据配置换协议
        //最大包长1024平方 长度位置偏移0字节 大小一个int
        return new JavaRPCDecoder(1024*1024,0,4);
    }

    public MessageToMessageEncoder getEncoder(){
        return new JavaRPCEncoder();
    }

    public RPCProtocol getRPCProtocol(){
        return new MZJavaProtocol();
    }

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
        this.zooKeeperHost = zooKeeperHost;
    }

    public String getServerHost() {
        //获取时带上端口
        return serverHost+":"+port;
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
