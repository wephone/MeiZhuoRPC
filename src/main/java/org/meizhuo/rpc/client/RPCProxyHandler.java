package org.meizhuo.rpc.client;

import org.meizhuo.rpc.Exception.RPCTimeOutException;
import org.meizhuo.rpc.core.RPC;
import org.meizhuo.rpc.protocol.IdUtils;
import org.meizhuo.rpc.server.RPCResponse;
import org.meizhuo.rpc.trace.SpanStruct;
import org.meizhuo.rpc.trace.TraceSendUtils;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by wephone on 17-12-26.
 */
public class RPCProxyHandler  implements InvocationHandler {

    //TODO 每次创建新代理这个变量好像都会被重新赋值
    private static AtomicLong requestTimes=new AtomicLong(0);//记录调用的次数 也作为ID标志
//    private RPCResponse rpcResponse=new RPCResponse();

    /**
     * 代理抽象接口调用的方法
     * 发送方法信息给服务端 加锁等待服务端返回
     * @param proxy
     * @param method
     * @param args
     * @return
     * @throws Throwable
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        RPCRequest request=new RPCRequest();
        request.setRequestID(IdUtils.getRequestId());
        request.setServiceId(RPC.getClientConfig().getServiceId(method.getDeclaringClass().getName()));//返回表示声明由此 Method 对象表示的方法的类或接口的Class对象
        request.setMethodName(method.getName());
//        request.setParameterTypes(method.getParameterTypes());//返回形参类型
        request.setParameters(args);//输入的实参
        //同步等待实现端返回的锁 改用request对象的对象锁 传值方便一点 不用lock和condition都传递
//        Lock lock = new ReentrantLock();
//        Condition condition=lock.newCondition();
//        System.out.println("Invoke Method Thread:"+Thread.currentThread().getName());
        SpanStruct span=TraceSendUtils.preClientSend(request);
        RPCRequestNet.getInstance().requestLockMap.put(request.getRequestID(),request);
//        lock.lock();//获取锁
        TraceSendUtils.clientSend(span);
        RPCRequestNet.getInstance().send(request);
//        lock.unlock();
        if (!request.getIsResponse()){
            //TODO 调用超时 触发重试或者熔断
            RPCRequestNet.getInstance().requestLockMap.remove(request.getRequestID());
            throw new RPCTimeOutException();
        }
        TraceSendUtils.clientReceived(request.getRpcResponse(),request);
        RPCRequestNet.getInstance().requestLockMap.remove(request.getRequestID());
        return request.getRpcResponse().getResult();//目标方法的返回结果
    }

    //生成请求的唯一ID
//    private String buildRequestID(String methodName){
//        StringBuilder sb=new StringBuilder();
//        sb.append(requestTimes.incrementAndGet());
//        sb.append(System.currentTimeMillis());
//        sb.append(methodName);
//        Random random = new Random();
//        sb.append(random.nextInt(1000));
//        return sb.toString();
//    }
}
