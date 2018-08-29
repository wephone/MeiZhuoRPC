package org.meizhuo.rpc.threadLocal;

import org.meizhuo.rpc.promise.Deferred;

public class NeedReturnThreadLocal {

    private static ThreadLocal<Boolean> needReturnThreadLocal=new ThreadLocal<>();

    public static Boolean needReturn(){
        Boolean needReturn=needReturnThreadLocal.get();
        if (needReturn==null){
            return true;
        }
        return needReturn;
    }

    public static void noNeedReturn(){
        needReturnThreadLocal.set(false);
    }

    public static void removeNeedReturn(){
        needReturnThreadLocal.remove();
    }

}
