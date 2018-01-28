package org.meizhuo.rpc.zksupport.LoadBalance;

import org.apache.zookeeper.ZooKeeper;
import org.meizhuo.rpc.Exception.ProvidersNoFoundException;

import java.util.List;

/**
 * Created by wephone on 18-1-18.
 */
public class PollingBalance implements LoadBalance {

    @Override
    public String chooseIP(String serviceName) throws ProvidersNoFoundException {
        return null;
    }

    @Override
    public void changeIP(String serviceName, List<String> newIP) {

    }
}
