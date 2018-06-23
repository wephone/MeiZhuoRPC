package promiseTest;

import org.meizhuo.rpc.promise.Promise;
import org.meizhuo.rpc.promise.ReturnType;

public interface TestFunction {

    @ReturnType(Integer.class)
    Promise remoteInteger();
    @ReturnType(String.class)
    Promise remoteString();

}
