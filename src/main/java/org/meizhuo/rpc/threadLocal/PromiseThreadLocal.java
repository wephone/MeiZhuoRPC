package org.meizhuo.rpc.threadLocal;

import org.meizhuo.rpc.promise.Deferred;
import org.meizhuo.rpc.promise.Promise;

public class PromiseThreadLocal {

    public static ThreadLocal<Deferred> promiseThreadLocal=new ThreadLocal<>();

    public static void setThreadPromise(Deferred promise){
        promiseThreadLocal.set(promise);
    }

    public static Deferred getThreadPromise(){
        return promiseThreadLocal.get();
    }

    public static void removeThreadPromise(){
        promiseThreadLocal.remove();
    }

}
