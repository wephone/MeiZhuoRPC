package org.meizhuo.rpc.client;

import org.springframework.beans.factory.FactoryBean;

import java.lang.reflect.Proxy;

public class RPCProxyBeanFactory implements FactoryBean {

    private Class RPCInterface;

    public Class getRPCInterface() {
        return RPCInterface;
    }

    public void setRPCInterface(Class RPCInterface) {
        this.RPCInterface = RPCInterface;
    }

    @Override
    public Object getObject() throws Exception {
        RPCProxyHandler handler=new RPCProxyHandler();
        Object proxyObj=Proxy.newProxyInstance(RPCInterface.getClassLoader(),new Class<?>[]{RPCInterface},handler);
        return proxyObj;
    }

    @Override
    public Class<?> getObjectType() {
        return RPCInterface;
    }

    @Override
    public boolean isSingleton() {
        //同步调用的采用单例
        return true;
    }
}
