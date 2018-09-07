package org.meizhuo.rpc.client;

import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.MessageToMessageEncoder;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;
import org.meizhuo.rpc.core.RPC;
import org.meizhuo.rpc.promise.Deferred;
import org.meizhuo.rpc.protocol.*;
import org.meizhuo.rpc.zksupport.LoadBalance.LoadBalance;
import org.meizhuo.rpc.zksupport.ZKConnect;
import org.meizhuo.rpc.zksupport.service.ServiceInfo;
import org.meizhuo.rpc.zksupport.service.ZKServerService;
import org.springframework.beans.BeansException;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.PropertyValue;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.TypedStringValue;
import org.springframework.beans.factory.support.*;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by wephone on 17-12-26.
 */
public class ClientConfig implements ApplicationContextAware,BeanDefinitionRegistryPostProcessor {

//    private String host;
//    private int port;
    //zookeeper集群地址 逗号隔开
    private String zooKeeperHost;
    //调用超时时间 默认3秒
    private long overtime=3000;
    /**
     * 远程调用接口全类名集合 用于启动时代理
     * key 服务ID 出于跨语言考虑 不能拿全类名作为服务标识 需要自行定义服务ID
     * value 服务java全类名
     * 用于用户配置 保证serviceId不回设置重复
     */
    private Map<String,String> serviceInterface;
    /**
     * 键值与上一对象相反
     * 用于通过服务名找到服务Id
     * 不可由用户配置
     */
    private Map<String,String> serviceNameForId;
    private LoadBalance loadBalance;
    private Integer poolMaxIdle=2;
    private Integer poolMaxTotal=4;
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

    public Map<String, String> getServiceInterface() {
        return serviceInterface;
    }

    public void setServiceInterface(Map<String, String> serviceInterface) {
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

    public ProtocolEnum getProtocol() {
        return protocol;
    }

    public void setProtocol(ProtocolEnum protocol) {
        this.protocol = protocol;
    }

    public String getServiceId(String serviceClass) {
        return serviceNameForId.get(serviceClass);
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
            RPC.zkConnect= new ZKConnect().clientConnect();
            ZKServerService zkServerService=new ZKServerService();
            Map<String,String> services=RPC.getClientConfig().getServiceInterface();
            serviceNameForId=new HashMap<>();
            //初始化所有可用IP 初始化读写锁
            for (Map.Entry<String,String> entry :services.entrySet()){
                System.out.println("init client bean "+entry.getValue()+"...");
                String serviceId=entry.getKey();
                List<String> ips=zkServerService.getAllServiceIP(serviceId);
//                for (String ip:ips){
//                    RPCRequestNet.getInstance().IPChannelMap.putIfAbsent(ip,new IPChannelInfo());
//                }
                ServiceInfo serviceInfo=new ServiceInfo();
                serviceInfo.setServiceIPSet(ips);
                ReadWriteLock readWriteLock=new ReentrantReadWriteLock();
                String serviceClass=entry.getValue();
                RPCRequestNet.getInstance().serviceLockMap.putIfAbsent(serviceId,readWriteLock);
                RPCRequestNet.getInstance().serviceNameInfoMap.putIfAbsent(serviceId,serviceInfo);
                serviceNameForId.put(serviceClass,serviceId);
                //生成对应类代理 注入spring
//                Class cls=Class.forName(serviceClass);
//                Annotation async=cls.getAnnotation(Async.class);
//                Object proxy;
//                if (async!=null){
//                    RPCProxyAsyncHandler handler=new RPCProxyAsyncHandler();
//                    proxy=Proxy.newProxyInstance(cls.getClassLoader(),new Class<?>[]{cls},handler);
//                }else {
//                    RPCProxyHandler handler=new RPCProxyHandler();
//                    proxy=Proxy.newProxyInstance(cls.getClassLoader(),new Class<?>[]{cls},handler);
//                }
//                //获取bean工厂
//                DefaultListableBeanFactory beanFactory = (DefaultListableBeanFactory)applicationContext.getAutowireCapableBeanFactory();
//                //校验bean
//                applicationContext.getAutowireCapableBeanFactory().applyBeanPostProcessorsAfterInitialization(proxy, serviceClass);
//                //以单例的形式注入bean
//                beanFactory.registerSingleton(serviceClass, proxy);
            }
            if (RPC.isTrace()){
                RuntimeMXBean runtimeMBean = ManagementFactory.getRuntimeMXBean();
                List<String> vmArgs=runtimeMBean.getInputArguments();
                for (String arg:vmArgs){
                    //必须设置vm参数才能启用链路追踪
                    if (arg.contains("transmittable-thread-local")){
                        RPC.getTraceConfig().setEnableTrace(true);
                        break;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (KeeperException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry beanDefinitionRegistry) throws BeansException {
        //todo 为何要加#0
        BeanDefinition beanDefinition=beanDefinitionRegistry.getBeanDefinition("org.meizhuo.rpc.client.ClientConfig#0");
        MutablePropertyValues mutablePropertyValues=beanDefinition.getPropertyValues();
        PropertyValue propertyValue=mutablePropertyValues.getPropertyValue("serviceInterface");
        Map<TypedStringValue,TypedStringValue> map= (Map) propertyValue.getValue();
        for (Map.Entry<TypedStringValue,TypedStringValue> entry:map.entrySet()){
            String serviceInterface=entry.getValue().getValue();
            //生成对应类代理 注入spring
            try {
                Class cls=Class.forName(serviceInterface);
                Annotation async=cls.getAnnotation(Async.class);
                BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(cls);
                GenericBeanDefinition definition = (GenericBeanDefinition) builder.getRawBeanDefinition();
                definition.getPropertyValues().add("RPCInterface", definition.getBeanClassName());
                definition.setAutowireMode(GenericBeanDefinition.AUTOWIRE_BY_TYPE);
                if (async!=null){
                    //异步代理
                    definition.setBeanClass(RPCProxyAsyncBeanFactory.class);
                }else {
                    //同步代理
                    definition.setBeanClass(RPCProxyBeanFactory.class);
                }
                beanDefinitionRegistry.registerBeanDefinition(serviceInterface, definition);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory configurableListableBeanFactory) throws BeansException {

    }
}
