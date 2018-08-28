package org.meizhuo.rpc.client;


import org.meizhuo.rpc.Exception.ProvidersNoFoundException;
import org.meizhuo.rpc.core.RPC;
import org.meizhuo.rpc.promise.Deferred;
import org.meizhuo.rpc.trace.NamedThreadFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Random;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public class RPCProxyAsyncHandler implements InvocationHandler {

    private static AtomicLong requestTimes=new AtomicLong(0);

    private static ThreadPoolExecutor asyncSendExecutor=new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors()*2,Runtime.getRuntime().availableProcessors()*3,15, TimeUnit.SECONDS,
            new LinkedBlockingDeque<>(),
            new NamedThreadFactory("AsyncSend"),
            new ThreadPoolExecutor.DiscardPolicy());

    private Deferred promise;

    public RPCProxyAsyncHandler(Deferred promise) {
        this.promise = promise;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) {
        //直接返回promise 其他操作全部异步
        asyncSendExecutor.submit(() -> {
            RPCRequest request=new RPCRequest();
            String requesrId=buildRequestID(method.getName());
            request.setRequestID(requesrId);
            request.setServiceId(RPC.getClientConfig().getServiceId(method.getDeclaringClass().getName()));//返回表示声明由此 Method 对象表示的方法的类或接口的Class对象
            request.setMethodName(method.getName());
//        request.setParameterTypes(method.getParameterTypes());//返回形参类型
            request.setParameters(args);//输入的实参
            RPCRequestNet.getInstance().promiseMap.put(request.getRequestID(),promise);
            try {
                //todo 异步调用的超时熔断
                RPCRequestNet.getInstance().asyncSend(request);
            } catch (ProvidersNoFoundException e) {
                //这里不输出日志 由failcallback处理
                Deferred deferred=RPCRequestNet.getInstance().promiseMap.get(requesrId);
                deferred.reject(e);
            } catch (Exception e){
                //暂时预留 目前尚未做其他异常封装处理
                e.printStackTrace();
            }
        });
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
