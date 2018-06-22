package promiseTest;

import org.junit.Test;
import org.meizhuo.rpc.core.RPC;
import org.meizhuo.rpc.promise.Promise;
import org.meizhuo.rpc.promise.PromiseImpl;
import org.meizhuo.rpc.promise.SucessCallBack;
import org.meizhuo.rpc.promise.ThenCallBack;

public class PromiseTest {

    @Test
    public void test(){
        PromiseImpl promiseImpl=new PromiseImpl();
        Promise promise=promiseImpl;
        TestFunction testFunction= (TestFunction) RPC.AsyncCall(TestFunction.class,promise);
        testFunction.rpcCall().then(new ThenCallBack() {
                    @Override
                    public Promise done() {
                        return testFunction.anotherRpcCall();
                    }
                })
                .then(() -> testFunction.rpcCall())
                .success(new SucessCallBack() {
            @Override
            public void done() {
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
                promiseImpl.resolve();
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
