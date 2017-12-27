package org.meizhuo.rpc.client;

import org.meizhuo.rpc.server.RPCResponse;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * Created by wephone on 17-12-26.
 */
public class RPCProxyHandler  implements InvocationHandler {

    private RPCResponse rpcResponse=new RPCResponse();

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
        request.setClassName(method.getDeclaringClass().getName());//返回表示声明由此 Method 对象表示的方法的类或接口的Class对象
        request.setMethodName(method.getName());
        request.setParameterTypes(method.getParameterTypes());//返回形参类型
        request.setParameters(args);//输入的实参
        return rpcResponse.getResult();//目标方法的返回结果
    }
}
