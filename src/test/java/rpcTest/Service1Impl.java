package rpcTest;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by wephone on 18-1-13.
 */
public class Service1Impl implements Service1 {

    private static AtomicInteger count=new AtomicInteger(0);

    @Override
    public void testVoid() {
        System.out.println("完全空的RPC调用");
    }

    @Override
    public void testVoid(String voidarg) {
        System.out.println("测试同名不同参数的方法:"+voidarg);
    }

    @Override
    public void testStringVoid(String a) {
        System.out.println("接收到字符:"+a);
    }

    @Override
    public Integer count() {
        Integer res=count.addAndGet(1);
        System.out.println("Service1 计数:"+res);
        return res;
    }
}
