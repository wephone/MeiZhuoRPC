package rpcTest;

import org.junit.Test;
import org.meizhuo.rpc.core.RPC;
import org.meizhuo.rpc.server.ServerConfig;

/**
 * Created by wephone on 17-12-30.
 */
public class Server {


    public static void main(String[] args){
        ServerConfig.port=9999;
        RPC.start();
    }

}
