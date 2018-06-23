package asyncRpcTest;

import org.meizhuo.rpc.promise.Promise;

public interface Function {

    Promise getInteger(String a);

    Promise getString(Integer a);

}
