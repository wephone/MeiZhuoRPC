package org.meizhuo.rpc.zksupport.LoadBalance;

import java.util.concurrent.*;
import java.util.concurrent.locks.ReadWriteLock;

/**
 * Created by wephone on 18-1-9.
 */
@Deprecated
public class BalanceThreadPool {

    //静态线程池
    private static ThreadPoolExecutor executor;

    public static ThreadPoolExecutor getThreadPool(){
        if (executor==null){
//            synchronized (executor){空对象不可作为对象锁
            synchronized (BalanceThreadPool.class){
                if (executor==null){
                    int cpuCoreNum=Runtime.getRuntime().availableProcessors();
                    executor=new ThreadPoolExecutor(cpuCoreNum,15,15, TimeUnit.SECONDS,
                            new LinkedBlockingDeque<>(),//LinkedBlockingDeque 无界队列 所以拒绝策略和最大线程数其实都没作用
                            new ThreadPoolExecutor.DiscardPolicy());
                }
            }
        }
        return executor;
    }

    public static void execute(Runnable runnable){
        getThreadPool().execute(runnable);
    }

    public static void shutDown(){
        getThreadPool().shutdown();
    }

}
