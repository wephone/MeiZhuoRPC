package rpcTest;

import org.junit.Test;
import org.meizhuo.rpc.core.RPC;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.concurrent.CountDownLatch;

/**
 * Created by wephone on 18-1-13.
 */
public class MultiClient {

    @Test
    public void start(){
        CountDownLatch countDownLatch=new CountDownLatch(1);
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(
                new String[] { "file:src/test/java/rpcTest/MultiServiceClientContext.xml" });
        context.start();
        Service1 service1= (Service1) RPC.call(Service1.class);
        Service2 service2= (Service2) RPC.call(Service2.class);
        service1.testVoid();
        service1.testVoid("void args");
        service1.testStringVoid("发送");
        String res=service2.testString(2.3);
        System.out.println(res);
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
