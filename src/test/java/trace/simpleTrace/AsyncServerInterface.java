package trace.simpleTrace;

import org.meizhuo.rpc.client.Async;
import org.meizhuo.rpc.promise.Promise;

@Async
public interface AsyncServerInterface {

    Promise remoteService(Double arg1, String arg2);
    Promise intMethodTest(int a);
    Promise IntegerMethodTest(Integer a);
    Promise intMethodIntegerArgsTest(Integer a);
    Promise stringMethodIntegerArgsTest(Integer a, Double b);

}
