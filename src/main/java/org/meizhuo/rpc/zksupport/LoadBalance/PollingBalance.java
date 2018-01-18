package org.meizhuo.rpc.zksupport.LoadBalance;

import org.apache.zookeeper.ZooKeeper;
import org.meizhuo.rpc.Exception.ProvidersNoFoundException;

/**
 * Created by wephone on 18-1-18.
 */
public class PollingBalance implements LoadBalance {
    @Override
    public void balance(ZooKeeper zooKeeper, String serviceName) {

    }

    @Override
    public String chooseIP(String serviceName) throws ProvidersNoFoundException {
        return null;
    }
}
