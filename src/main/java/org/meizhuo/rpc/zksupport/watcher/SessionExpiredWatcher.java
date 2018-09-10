package org.meizhuo.rpc.zksupport.watcher;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.meizhuo.rpc.client.RPCRequestNet;
import org.meizhuo.rpc.core.RPC;
import org.meizhuo.rpc.zksupport.ZKConst;
import org.meizhuo.rpc.zksupport.service.ServiceInfo;
import org.meizhuo.rpc.zksupport.service.ZKServerService;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

/**
 * 监听session过期 用于段先重连
 */
public class SessionExpiredWatcher implements Watcher {

    private String host;
    private CountDownLatch countDownLatch;
    private boolean reconnect=false;
    private ZooKeeper reconnectZk;

    public SessionExpiredWatcher(String host,CountDownLatch countDownLatch) {
        this.host = host;
        this.countDownLatch=countDownLatch;
    }

    @Override
    public void process(WatchedEvent watchedEvent) {
        if (watchedEvent.getState()== Event.KeeperState.SyncConnected){
            if (countDownLatch.getCount()>0) {
                System.out.println("zookeeper connect success");
                countDownLatch.countDown();
            }
            if (reconnect){
                RPC.zkConnect=reconnectZk;
                ZKServerService zkServerService=new ZKServerService();
                if (RPC.serverContext!=null) {
                    try {
                        zkServerService.initZnode();
                        //创建所有提供者服务的znode
                        zkServerService.createServerService();
                    } catch (KeeperException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.println("zookeeper recreate service infos success");
                }else if (RPC.clientContext!=null){
                    //重新监听各个服务信息
                    Map<String,String> services=RPC.getClientConfig().getServiceInterface();
                    for (Map.Entry<String,String> entry :services.entrySet()){
                        String serviceId=entry.getKey();
                        try {
                            //重新注册一个watcher 更改IP todo 考虑做不做服务可用ip的更改
                            zkServerService.getAllServiceIP(serviceId);
                            System.out.println("zookeeper recreate client infos success");
                        } catch (KeeperException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
                System.out.println("zookeeper reconnect success");
            }
        }else if (watchedEvent.getState()== Event.KeeperState.Expired){
            //session超时
            try {
                reconnectZk =new ZooKeeper(host, ZKConst.sessionTimeout,this);
                reconnect=true;
                System.out.println("zookeeper reconnecting...");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else {
            //TODO 出现disconnect状态
            System.out.println("zookeeper state:"+watchedEvent.getState());
        }
    }
}
