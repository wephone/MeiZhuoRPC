package rpcTest;

import org.junit.Test;
import org.meizhuo.rpc.core.RPC;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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

    @Test
    public void multiThread(){
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(
                new String[] { "file:src/test/java/rpcTest/MultiServiceClientContext.xml" });
        context.start();
        CountDownLatch countDownLatch=new CountDownLatch(1);
//        Service1 service1= (Service1) RPC.call(Service1.class);
//        Service2 service2= (Service2) RPC.call(Service2.class);
        ExecutorService executorService= Executors.newFixedThreadPool(8);
        for (int i = 0; i <1000 ; i++) {
            int finalI = i+1;
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    Service1 service1= (Service1) RPC.call(Service1.class);
                    Service2 service2= (Service2) RPC.call(Service2.class);
                    System.out.println("第"+ finalI +"次发出请求");
                    service1.count();
                    service2.count();
                }
            });
        }
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void timer(){
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(
                new String[] { "file:src/test/java/rpcTest/MultiServiceClientContext.xml" });
        context.start();
        CountDownLatch countDownLatch=new CountDownLatch(1);
        Service1 service1= (Service1) RPC.call(Service1.class);
        Service2 service2= (Service2) RPC.call(Service2.class);
        for (int i = 0; i <100 ; i++) {
            try {
                service1.count();
                service2.count();
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void service2Thread(){
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(
                new String[] { "file:src/test/java/rpcTest/MultiServiceClientContext.xml" });
        context.start();
        CountDownLatch countDownLatch=new CountDownLatch(1);
        for (int i = 0; i <1000 ; i++) {
            int finalI = i+1;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Service2 service2= (Service2) RPC.call(Service2.class);
                    System.out.println("第"+ finalI +"次发送请求");
                    service2.count();
                }
            }).start();
        }
    }

    @Test
    public void millionThread(){
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(
                new String[] { "file:src/test/java/rpcTest/MultiServiceClientContext.xml" });
        context.start();
        CountDownLatch countDownLatch=new CountDownLatch(1);
//        Service1 service1= (Service1) RPC.call(Service1.class);
//        Service2 service2= (Service2) RPC.call(Service2.class);
        ExecutorService executorService= Executors.newFixedThreadPool(32);
        for (int i = 0; i <1000000 ; i++) {
            int finalI = i+1;
            Runnable run=new Runnable() {
                @Override
                public void run() {
                    Service1 service1= (Service1) RPC.call(Service1.class);
                    Service2 service2= (Service2) RPC.call(Service2.class);
                    System.out.println("第"+ finalI +"次发出请求");
                    service1.count();
                    service2.count();
                }
            };
            executorService.execute(run);
//            new Thread(run).start();
        }
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
