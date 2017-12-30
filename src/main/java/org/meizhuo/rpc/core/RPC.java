package org.meizhuo.rpc.core;

import com.google.gson.Gson;
import org.meizhuo.rpc.client.RPCProxyHandler;
import org.meizhuo.rpc.client.RPCRequest;

import java.lang.reflect.Proxy;

/**
 * Created by wephone on 17-12-26.
 */
public class RPC {

    private static Gson gson=new Gson();

    /**
     * 暴露调用端使用的静态方法 为抽象接口生成动态代理对象
     * TODO 考虑后面优化不在使用时仍需强转
     * @param cls 抽象接口的类类型
     * @return 接口生成的动态代理对象
     */
    public static Object call(Class cls){
        RPCProxyHandler handler=new RPCProxyHandler();
        Object proxyObj=Proxy.newProxyInstance(cls.getClassLoader(),new Class<?>[]{cls},handler);
        return proxyObj;
    }

    /**
     * 实现端启动RPC服务
     */
    public static void start(){
        System.out.println("welcome to use MeiZhuoRPC");
    }

    public static String requestEncode(RPCRequest request){
        return gson.toJson(request)+System.getProperty("line.separator");
    }
}
