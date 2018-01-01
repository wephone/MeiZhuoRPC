package rpcTest;

/**
 * Created by wephone on 17-12-30.
 */
public interface Service {

    //TODO json会自动把数据变为Double 这里先写着Double 而且还是包装类
    Double remoteService(Double arg1,String arg2);
    int intMethodTest(int a);
    Integer IntegerMethodTest(Integer a);
    int intMethodIntegerArgsTest(Integer a);
}
