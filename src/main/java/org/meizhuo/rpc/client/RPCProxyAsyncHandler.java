package org.meizhuo.rpc.client;


import org.meizhuo.rpc.promise.Promise;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class RPCProxyAsyncHandler implements InvocationHandler {

    Promise promise;

    public RPCProxyAsyncHandler(Promise promise) {
        this.promise = promise;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        System.out.println("then done right now......");
        return promise;
    }
}
