package rpcTest;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by wephone on 18-1-13.
 */
public class Service2Impl implements Service2{

    private static AtomicInteger count=new AtomicInteger(0);

    @Override
    public String testString(Double a) {
        double b=a*2;
        return b+"";
    }

    @Override
    public Integer count() {
        Integer res=count.addAndGet(1);
        System.out.println("Service2 计数:"+res);
        return res;
    }
}
