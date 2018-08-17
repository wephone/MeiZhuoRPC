package asyncRpcTest;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.meizhuo.rpc.core.RPC;
import org.meizhuo.rpc.promise.*;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import rpcTest.Service;

import java.util.concurrent.CountDownLatch;

/**
 * Created by wephone on 17-12-30.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"file:src/test/java/asyncRpcTest/ClientContext.xml"})
public class Client {

    @Test
    public void start(){
        CountDownLatch countDownLatch=new CountDownLatch(1);
        Deferred deferred =new Deferred();
        Function function= (Function) RPC.AsyncCall(Function.class,deferred);
        function.getInteger("first args")
                .then(new ThenCallBack<Integer>(){
                    @Override
                    public Promise done(Integer data) {
                        //将得到的结果作为参数 将返回的结果作为下一层调用的参数
                        return function.getString(data);
                    }
                }).then(new ThenCallBack<String>() {
                    @Override
                    public Promise done(String data) {
                        return function.getInteger(data);
                    }
                }).success(new SucessCallBack<Integer>() {
                    @Override
                    public void done(Integer result) {
                        System.out.println("result:"+result);
                    }
                }).fail(new FailCallback() {
                    @Override
                    public void done(Exception e) {
                        System.out.println("rpc fail!");
                        e.printStackTrace();
                    }
                });
        System.out.println("main thread finish!");
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void lamdaStart(){
        CountDownLatch countDownLatch=new CountDownLatch(1);
        Deferred deferred =new Deferred();
        Function function= (Function) RPC.AsyncCall(Function.class,deferred);
        function.getInteger("first args")
                .then((ThenCallBack<Integer>) data -> function.getString(data))
                .then((ThenCallBack<String>) data -> function.getInteger(data))
                .success((SucessCallBack<Integer>) result -> System.out.println("result:"+result));
        System.out.println("main thread finish!");
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
