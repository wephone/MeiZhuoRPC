package org.meizhuo.rpc.client;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * Created by wephone on 17-12-26.
 */
public class RPCProxyHandler  implements InvocationHandler {
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
        return null;
    }
}
