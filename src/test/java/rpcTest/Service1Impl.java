package rpcTest;

/**
 * Created by wephone on 18-1-13.
 */
public class Service1Impl implements Service1 {
    @Override
    public void testVoid() {
        System.out.println("完全空的RPC调用");
    }

    @Override
    public void testStringVoid(String a) {
        System.out.println("接收到字符:"+a);
    }
}
