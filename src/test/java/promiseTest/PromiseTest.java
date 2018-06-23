package promiseTest;

import org.junit.Test;
import org.meizhuo.rpc.core.RPC;
import org.meizhuo.rpc.promise.Promise;
import org.meizhuo.rpc.promise.Deferred;
import org.meizhuo.rpc.promise.SucessCallBack;
import org.meizhuo.rpc.promise.ThenCallBack;

public class PromiseTest {

    @Test
    public void test(){
        Deferred deferred =new Deferred();
//        Promise promise= deferred.promise();
        TestFunction testFunction= (TestFunction) RPC.AsyncCall(TestFunction.class,deferred);
        testFunction.remoteInteger().then(new ThenCallBack<Integer>() {
                    @Override
                    public Promise done(Integer data) {
                        System.out.println(data);
                        return testFunction.remoteString();
                    }
                })
                .then((ThenCallBack<String>) data -> {
                    System.out.println(data);
                    return testFunction.remoteInteger();
                })
                .success(new SucessCallBack<Integer>() {
                    @Override
                    public void done(Integer res) {
                        System.out.println("done callback");
                    }
                });
        new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("thread start");
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                deferred.resolve(2);
                System.out.println("task finish 2 second");
            }
        }).start();
        System.out.println("异步完成");
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
