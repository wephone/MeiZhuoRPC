package trace.simpleTrace;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.meizhuo.rpc.promise.NextCallBack;
import org.meizhuo.rpc.promise.Promise;
import org.meizhuo.rpc.promise.SucessCallBack;
import org.meizhuo.rpc.promise.ThenCallBack;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import rpcTest.Service;

import java.util.concurrent.CountDownLatch;

/**
 * Created by wephone on 17-12-30.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"file:src/test/java/trace/simpleTrace/ClientContext.xml"})
public class Client {

    @Autowired
    ServiceInterface service;
    @Autowired
    AsyncServerInterface asyncServer;

    @Test
    public void start(){
        CountDownLatch countDownLatch=new CountDownLatch(1);
//        Service service= (Service) RPC.call(Service.class);
        System.out.println("RPC span1:"+service.remoteService(233.0,"hhh"));
        System.out.println("RPC span2:"+service.IntegerMethodTest(233));
        System.out.println("RPC span3:"+service.stringMethodIntegerArgsTest(233,666.66));
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void asyncStart(){
        CountDownLatch countDownLatch=new CountDownLatch(1);
        asyncServer.remoteService(233.0,"hhh")
                .then(new NextCallBack<Double>() {
                    @Override
                    public Promise nextRPC(Double arg) {
                        System.out.println("double arg:"+arg);
                        return asyncServer.stringMethodIntegerArgsTest(123,arg);
                    }
                })
                .success(new SucessCallBack<String>() {
                    @Override
                    public void done(String result) {
                        System.out.println("string result:"+result);
                    }
                });
        System.out.println("main thread finish");
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void asyncAsyncStart(){
        CountDownLatch countDownLatch=new CountDownLatch(1);
        asyncServer.remoteService(233.0,"hhh")
                .then(new ThenCallBack<Double,Double>() {
                    @Override
                    public Double done(Double arg) {
                        //不等待结果得再调用下一个异步rpc 例如日志记录等等
                        return arg;
                    }
                })
                .then(new NextCallBack<Double>() {
                    @Override
                    public Promise nextRPC(Double arg) {
                        System.out.println("double arg:"+arg);
                        return asyncServer.stringMethodIntegerArgsTest(123,arg);
                    }
                })
                .success(new SucessCallBack<String>() {
                    @Override
                    public void done(String result) {
                        System.out.println("string result:"+result);
                    }
                });
        System.out.println("main thread finish");
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
