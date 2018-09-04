package org.meizhuo.rpc.server;

import org.meizhuo.rpc.client.RPCRequest;
import org.meizhuo.rpc.core.RPC;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by wephone on 18-1-1.
 */
public class InvokeServiceUtil {

    /**
     * 从spring中取出实现类 反射调用相应实现类并结果
     * @param request
     * @return
     */
    public static Object invoke(RPCRequest request){
        Object result=null;//内部变量必须赋值 全局变量才不用
        //实现类Bean
        Object implClassBean= RPC.getServerConfig().getServerImplMap().get(request.getServiceId());
        try {
            Class implClass=Class.forName(implClassBean.getClass().getName());
            Object[] parameters=request.getParameters();
            if (parameters.length==0){
                //无参方法
                Method method=implClass.getDeclaredMethod(request.getMethodName());
//                Object implObj=RPC.serverContext.getBean(implClass);
                result=method.invoke(implClassBean);
            }else {
                int parameterNums=request.getParameters().length;
                Class[] parameterTypes=new Class[parameterNums];
                for (int i = 0; i <parameterNums ; i++) {
                    parameterTypes[i]=parameters[i].getClass();
                }
                Method method=implClass.getDeclaredMethod(request.getMethodName(),parameterTypes);
//                Object implObj=RPC.serverContext.getBean(implClass);
                result=method.invoke(implClassBean,parameters);
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return result;
    }

}
