package trace.simpleTrace;

import rpcTest.Service;

/**
 * Created by wephone on 18-1-1.
 */
public class ServiceImpl implements Service {
    @Override
    public Double remoteService(Double arg1, String arg2) {
        return 1234567.0;
    }

    @Override
    public int intMethodTest(int a) {
        return a+1;
    }

    @Override
    public Integer IntegerMethodTest(Integer a) {
        return a*2;
    }

    @Override
    public int intMethodIntegerArgsTest(Integer a) {
        return a+10086;
    }

    @Override
    public String stringMethodIntegerArgsTest(Integer a, Double b) {
        return "String"+a+b;
    }
}
