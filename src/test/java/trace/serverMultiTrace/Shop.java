package trace.serverMultiTrace;

import org.junit.Test;
import org.meizhuo.rpc.core.RPC;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.IOException;

/**
 * Created by wephone on 17-12-30.
 */
//@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration(locations={"file:src/test/java/rpcTest/ShopContext.xml"})
public class Shop {

    @Test
    public void shopStart() throws InterruptedException, IOException {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(
                new String[] { "file:src/test/java/trace/serverMultiTrace/ShopContext.xml" });
        context.start();
//        ShopImpl shop= context.getBean(ShopImpl.class);
        //启动spring后才可启动 防止容器尚未加载完毕
        RPC.start();
    }


}
