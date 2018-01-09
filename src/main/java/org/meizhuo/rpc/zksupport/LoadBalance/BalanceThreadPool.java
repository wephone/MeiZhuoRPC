package org.meizhuo.rpc.zksupport.LoadBalance;

import java.util.concurrent.*;
import java.util.concurrent.locks.ReadWriteLock;

/**
 * Created by wephone on 18-1-9.
 * 平衡连接线程池 无论哪种平衡策略 都需要线程池和读写锁
 * 因为每个服务的提供者调用者互不关联 所以每个服务单独一个线程进行平衡操作
 * 即多线程处理多个服务的平衡 单线程处理各个单独服务
 * 每个服务内部用读写锁保证线程安全
 */
public class BalanceThreadPool {

    //全局读写锁 平衡连接时为写操作 负载均衡选中IP为读操作
    public static ConcurrentHashMap<String,ReadWriteLock> serviceLockMap=new ConcurrentHashMap<>();
    //静态线程池
    private static ThreadPoolExecutor executor;

    public static ThreadPoolExecutor getThreadPool(){
        if (executor==null){
            synchronized (executor){
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
