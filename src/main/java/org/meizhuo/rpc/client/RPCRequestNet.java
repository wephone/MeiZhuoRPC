package org.meizhuo.rpc.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import org.meizhuo.rpc.Exception.ProvidersNoFoundException;
import org.meizhuo.rpc.core.RPC;
import org.meizhuo.rpc.promise.Deferred;
import org.meizhuo.rpc.zksupport.LoadBalance.LoadBalance;
import org.meizhuo.rpc.zksupport.service.ServiceInfo;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;


/**
 * Created by wephone on 17-12-27.
 * 单例RPC请求类 调用端通过此单例进行对提供者端的请求
 * 有些属性需要是全局的例如requestLockMap 所以这里是单例的
 */
public class RPCRequestNet {

    //全局map 每个请求对应的锁 用于同步等待每个异步的RPC请求
    public Map requestLockMap=new ConcurrentHashMap<String,RPCRequest>();
    //异步RPC的凭据对象Map
    public Map<String,Deferred> promiseMap=new ConcurrentHashMap<String,Deferred>();
    //每个IP对应一个锁 防止重复连接一个IP多次
    public Map<String,Lock> connectlock=new ConcurrentHashMap<String,Lock>();
    //每个ip对应一个连接池
    public Map<String,ConnectionPool> connectionPoolMap=new ConcurrentHashMap<String,ConnectionPool>();
    //服务名称 映射 服务信息类
    public Map<String,ServiceInfo> serviceNameInfoMap=new ConcurrentHashMap<>();
    //IP地址 映射 对应的NIO Channel及其引用次数
    public Map<String,IPChannelInfo> IPChannelMap=new ConcurrentHashMap<>();
    //全局读写锁 更新ip时为写操作 负载均衡选中IP为读操作
    public ConcurrentHashMap<String,ReadWriteLock> serviceLockMap=new ConcurrentHashMap<>();
//    public CountDownLatch countDownLatch=new CountDownLatch(1);
    private LoadBalance loadBalance;
    private static RPCRequestNet instance;

    private RPCRequestNet() {
        loadBalance=RPC.getClientConfig().getLoadBalance();
    }

    //负载均衡获取对应IP 从连接池中获取连接channel
    private Channel connect(String ip) throws Exception {
        String[] IPArr=ip.split(":");
        String host=IPArr[0];
        Integer port=Integer.valueOf(IPArr[1]);
        if (connectionPoolMap.get(ip)==null){
            ConnectionPool connectionPool = new ConnectionPool(host, port);
            connectionPoolMap.putIfAbsent(ip, connectionPool);
        }
        return connectionPoolMap.get(ip).getChannel();
    }

    //单例模式 避免重复连接 构造方法中进行连接操作
    public static RPCRequestNet getInstance(){
        if (instance==null){
            synchronized (RPCRequest.class){
                if (instance==null){
                    instance=new RPCRequestNet();
                }
            }
        }
        return instance;
    }

    //向实现端发送请求
    public void send(RPCRequest request) throws ProvidersNoFoundException {
        String serviceName=request.getClassName();
        String ip=loadBalance.chooseIP(serviceName);
//        System.out.println("Send RPC Thread:"+Thread.currentThread().getName());
        try {
            //编解码对象为json 发送请求
            String requestJson= null;
            try {
                requestJson = RPC.requestEncode(request);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
            ByteBuf requestBuf= Unpooled.copiedBuffer(requestJson.getBytes());
            Channel channel=connect(ip);
            channel.writeAndFlush(requestBuf);
            connectionPoolMap.get(ip).releaseChannel(channel);
//            System.out.println("调用"+request.getRequestID()+"已发送");
            //挂起等待实现端处理完毕返回
            synchronized (request) {
                //放弃对象锁 并阻塞等待notify
                //TODO 后续配置因服务节点宕机导致的超时 给一次机会重试
                request.wait(RPC.getClientConfig().getOvertime());
            }
//            System.out.println("调用"+request.getRequestID()+"接收完毕");
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void asyncSend(RPCRequest request) throws ProvidersNoFoundException {
        String serviceName=request.getClassName();
        String ip=loadBalance.chooseIP(serviceName);
        try {
            String requestJson= null;
            requestJson = RPC.requestEncode(request);
            ByteBuf requestBuf= Unpooled.copiedBuffer(requestJson.getBytes());
            Channel channel=connect(ip);
            channel.writeAndFlush(requestBuf);
            connectionPoolMap.get(ip).releaseChannel(channel);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
