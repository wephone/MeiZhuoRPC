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
     * 反射调用相应实现类并结果
     * @param request
     * @return
     */
    public static Object invoke(RPCRequest request){
        Object result=null;//内部变量必须赋值 全局变量才不用
        //实现类名
        String implClassName= RPC.getServerConfig().getServerImplMap().get(request.getClassName());
        try {
            Class implClass=Class.forName(implClassName);
            Object[] parameters=request.getParameters();
            if (parameters==null){
                //无参方法
                Method method=implClass.getDeclaredMethod(request.getMethodName());
                Object implObj=implClass.newInstance();
                result=method.invoke(implObj);
            }else {
                int parameterNums=request.getParameters().length;
                Class[] parameterTypes=new Class[parameterNums];
                for (int i = 0; i <parameterNums ; i++) {
                    parameterTypes[i]=parameters[i].getClass();
                }
                Method method=implClass.getDeclaredMethod(request.getMethodName(),parameterTypes);
                Object implObj=implClass.newInstance();
                result=method.invoke(implObj,parameters);
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return result;
    }

}
