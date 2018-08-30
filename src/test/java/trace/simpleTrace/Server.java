package trace.simpleTrace;

import org.junit.Test;
import org.meizhuo.rpc.core.RPC;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.IOException;

/**
 * Created by wephone on 17-12-30.
 */
//@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration(locations={"file:src/test/java/rpcTest/ServerContext.xml"})
public class Server {

    @Test
    public void start() throws InterruptedException, IOException {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(
                new String[] { "file:src/test/java//trace/simpleTrace/ServerContext.xml" });
        context.start();
        //启动spring后才可启动 防止容器尚未加载完毕
        RPC.start();
    }


}
