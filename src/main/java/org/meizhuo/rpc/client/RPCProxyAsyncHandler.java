package org.meizhuo.rpc.client;


import org.meizhuo.rpc.promise.Deferred;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

public class RPCProxyAsyncHandler implements InvocationHandler {

    private static AtomicLong requestTimes=new AtomicLong(0);

    private Deferred promise;

    public RPCProxyAsyncHandler(Deferred promise) {
        this.promise = promise;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        RPCRequest request=new RPCRequest();
        request.setRequestID(buildRequestID(method.getName()));
        request.setClassName(method.getDeclaringClass().getName());//返回表示声明由此 Method 对象表示的方法的类或接口的Class对象
        request.setMethodName(method.getName());
//        request.setParameterTypes(method.getParameterTypes());//返回形参类型
        request.setParameters(args);//输入的实参
        RPCRequestNet.getInstance().promiseMap.put(request.getRequestID(),promise);
        RPCRequestNet.getInstance().asyncSend(request);
        return promise;
    }

    //生成请求的唯一ID
    private String buildRequestID(String methodName){
        StringBuilder sb=new StringBuilder();
        sb.append(requestTimes.incrementAndGet());
        sb.append(System.currentTimeMillis());
        sb.append(methodName);
        Random random = new Random();
        sb.append(random.nextInt(1000));
        return sb.toString();
    }
}
