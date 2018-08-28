package trace;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.meizhuo.rpc.core.RPC;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"file:src/test/java/trace/SecondServerContext.xml"})
public class SecondServer {

    @Test
    public void startSecondServer() throws IOException, InterruptedException {
        RPC.start();
        System.out.println(RPC.clientContext==RPC.serverContext);
    }

}
