package promiseTest;

import org.meizhuo.rpc.promise.Promise;

public interface TestFunction {

    Promise rpcCall();

    Promise anotherRpcCall();

}
