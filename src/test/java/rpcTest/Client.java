package rpcTest;

import org.junit.Test;
import org.meizhuo.rpc.client.ClientConfig;
import org.meizhuo.rpc.core.RPC;

/**
 * Created by wephone on 17-12-30.
 */
public class Client {

    public static void main(String[] args){
        ClientConfig.host="127.0.0.1";
        ClientConfig.port=9999;
        Service service= (Service) RPC.call(Service.class);
        service.remoteService(233,"hhh");
    }

}
