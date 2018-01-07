package org.meizhuo.rpc.core;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
//import com.google.gson.Gson;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;
import org.meizhuo.rpc.client.ClientConfig;
import org.meizhuo.rpc.client.RPCProxyHandler;
import org.meizhuo.rpc.client.RPCRequest;
import org.meizhuo.rpc.server.RPCResponse;
import org.meizhuo.rpc.server.RPCResponseNet;
import org.meizhuo.rpc.server.ServerConfig;
import org.meizhuo.rpc.zksupport.ZKConnect;
import org.meizhuo.rpc.zksupport.service.ZKServerService;
import org.springframework.context.ApplicationContext;

import java.io.IOException;
import java.lang.reflect.Proxy;
import java.util.concurrent.CountDownLatch;

/**
 * Created by wephone on 17-12-26.
 */
public class RPC {

    private static ObjectMapper objectMapper=new ObjectMapper();
//    private static Gson gson=new Gson();
    public static ApplicationContext serverContext;
    public static ApplicationContext clientContext;
    /**
     * 暴露调用端使用的静态方法 为抽象接口生成动态代理对象
     * TODO 考虑后面优化不在使用时仍需强转
     * @param cls 抽象接口的类类型
     * @return 接口生成的动态代理对象
     */
    public static Object call(Class cls){
        RPCProxyHandler handler=new RPCProxyHandler();
        Object proxyObj=Proxy.newProxyInstance(cls.getClassLoader(),new Class<?>[]{cls},handler);
        return proxyObj;
    }

    /**
     * 实现端启动RPC服务
     */
    public static void start() throws InterruptedException, IOException {
        CountDownLatch countDownLatch=new CountDownLatch(1);
        System.out.println("welcome to use MeiZhuoRPC");
        RPCResponseNet.connect();
        ZooKeeper zooKeeper= new ZKConnect().serverConnect();
        ZKServerService zkServerService=new ZKServerService(zooKeeper);
        try {
            //创建所有提供者服务的znode
            zkServerService.createServerService();
        } catch (KeeperException e) {
            e.printStackTrace();
        }
        //阻塞服务端不会退出
        countDownLatch.await();
    }

    public static String requestEncode(RPCRequest request) throws JsonProcessingException {
        return objectMapper.writeValueAsString(request)+System.getProperty("line.separator");
    }

    public static RPCRequest requestDeocde(String json) throws IOException {
        return objectMapper.readValue(json,RPCRequest.class);
    }

    public static String responseEncode(RPCResponse response) throws JsonProcessingException {
        return objectMapper.writeValueAsString(response)+System.getProperty("line.separator");
    }

    public static Object responseDecode(String json) throws IOException {
        return objectMapper.readValue(json,RPCResponse.class);
    }

    public static ServerConfig getServerConfig(){
        return serverContext.getBean(ServerConfig.class);
    }

    public static ClientConfig getClientConfig(){
        return clientContext.getBean(ClientConfig.class);
    }
}
