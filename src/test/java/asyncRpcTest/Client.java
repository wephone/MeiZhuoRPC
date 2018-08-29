package asyncRpcTest;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.meizhuo.rpc.core.RPC;
import org.meizhuo.rpc.promise.*;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    Function function;

    @Test
    public void start(){
        CountDownLatch countDownLatch=new CountDownLatch(1);
//        Deferred deferred =new Deferred();
//        Function function= (Function) RPC.AsyncCall(Function.class,deferred);
        function.getInteger("first args")
                .then(new NextCallBack<Integer>(){
                    @Override
                    public Promise nextRPC(Integer data) {
                        //将得到的结果作为参数 将返回的结果作为下一层调用的参数
                        //一个回调里出现2个以上的异步RPC时 需要2个都执行完毕时才会触发下一个then或next
                        function.getString(data);
                        return function.getString(data);
                    }
                })
                .then(new ThenCallBack<String,String>() {
                    @Override
                    public String done(String data) {
                        //若不按规则 then里出现异步RPC调用 则需要等RPC响应后才可触发下一个then或next
                        System.out.println("模拟查询数据库操作......");
                        return data;
                    }
                })
                .then(new NextCallBack<String>() {
                    @Override
                    public Promise nextRPC(String data) {
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
        function.getInteger("first args")
                .then((NextCallBack<Integer>) arg -> {
                    //将得到的结果作为参数 将返回的结果作为下一层调用的参数
                    return function.getString(arg);
                })
                .then((ThenCallBack<String, String>) arg -> {
                    System.out.println("模拟查询数据库操作......");
                    return arg;
                })
                .then((NextCallBack<String>) data -> function.getInteger(data))
                .success((SucessCallBack<Integer>) result -> System.out.println("result:"+result))
                .fail(e -> {
                    System.out.println("rpc fail!");
                    e.printStackTrace();
                });
        System.out.println("main thread finish!");
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
