package trace;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.meizhuo.rpc.core.RPC;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"file:src/test/java/trace/ThirdServerContext.xml"})
public class ThirdServer {

    @Test
    public void startThirdServer() throws IOException, InterruptedException {
        RPC.start();
    }

}
