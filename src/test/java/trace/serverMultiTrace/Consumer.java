package trace.serverMultiTrace;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import rpcTest.Service;

import java.util.concurrent.CountDownLatch;

/**
 * Created by wephone on 17-12-30.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"file:src/test/java/trace/serverMultiTrace/ConsumerContext.xml"})
public class Consumer {

    @Autowired
    ShopInterface shopInterface;

    @Test
    public void consumerStart(){
        CountDownLatch countDownLatch=new CountDownLatch(1);
//        Service service= (Service) RPC.call(Service.class);
        System.out.println("Buy Success"+shopInterface.buyFood());
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
