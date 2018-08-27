package asyncRpcTest;

import org.meizhuo.rpc.client.Async;
import org.meizhuo.rpc.promise.Promise;

@Async
public interface Function {

    Promise getInteger(String a);

    Promise getString(Integer a);

}
