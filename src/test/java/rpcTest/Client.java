package rpcTest;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.meizhuo.rpc.client.ClientConfig;
import org.meizhuo.rpc.core.RPC;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Created by wephone on 17-12-30.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"file:src/test/java/rpcTest/ClientContext.xml"})
public class Client {

    @Test
    public void start(){
        Service service= (Service) RPC.call(Service.class);
        System.out.println("RPC接收成功:"+service.remoteService(233.0,"hhh"));
    }

}
