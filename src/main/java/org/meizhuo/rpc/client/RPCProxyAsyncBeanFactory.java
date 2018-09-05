package org.meizhuo.rpc.client;

import org.springframework.beans.factory.FactoryBean;

import java.lang.reflect.Proxy;

public class RPCProxyAsyncBeanFactory implements FactoryBean {

    private Class RPCInterface;

    public Class getRPCInterface() {
        return RPCInterface;
    }

    public void setRPCInterface(Class RPCInterface) {
        this.RPCInterface = RPCInterface;
    }

    @Override
    public Object getObject() throws Exception {
        RPCProxyAsyncHandler handler=new RPCProxyAsyncHandler();
        Object proxyObj=Proxy.newProxyInstance(RPCInterface.getClassLoader(),new Class<?>[]{RPCInterface},handler);
        return proxyObj;
    }

    @Override
    public Class<?> getObjectType() {
        return RPCInterface;
    }

    @Override
    public boolean isSingleton() {
        //异步调用不采用单例 每个都需独立的promise
        return false;
    }
}
