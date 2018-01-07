package org.meizhuo.rpc.zksupport;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by wephone on 18-1-7.
 * 负载均衡操作类 保证每个提供者都被连接使用到
 */
public class LoadBalance {

    private static volatile LoadBalance instance;
    //提供者和调用者的数量
    private AtomicLong clientCount;
    private AtomicLong serverCount;

    private LoadBalance() {
    }

    public static LoadBalance getInstance(){
        if (instance==null){
            synchronized (LoadBalance.class) {
                if (instance == null) {
                    instance=new LoadBalance();
                }
            }
        }
        return instance;
    }

    public long getClientCount() {
        return clientCount.get();
    }

    public void setClientCount(long clientCount) {
        this.clientCount.set(clientCount);
    }

    public long getServerCount() {
        return serverCount.get();
    }

    public void setServerCount(long serverCount) {
        this.serverCount.set(serverCount);
    }
}
