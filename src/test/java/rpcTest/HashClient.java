package rpcTest;

import org.junit.Test;
import org.meizhuo.rpc.Exception.ProvidersNoFoundException;
import org.meizhuo.rpc.core.RPC;
import org.meizhuo.rpc.zksupport.LoadBalance.ConsistentHashing;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by wephone on 18-1-29.
 */
public class HashClient {

    @Test
    public void testHash(){
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(
                new String[] { "file:src/test/java/rpcTest/HashContext.xml" });
        context.start();
        ConsistentHashing consistentHashing=new ConsistentHashing();
//        consistentHashing.setConsumerIP("10.211.0.1:222");
//        consistentHashing.setConsumerIP("10.211.0.1:3333");
        try {
            System.out.println(consistentHashing.chooseIP("rpcTest.Service2"));
        } catch (ProvidersNoFoundException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void hash(){
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(
                new String[] { "file:src/test/java/rpcTest/HashContext.xml" });
        context.start();
        for (int i = 0; i <50 ; i++) {
            Service2 service2= context.getBean(Service2.class);
            service2.count();
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
