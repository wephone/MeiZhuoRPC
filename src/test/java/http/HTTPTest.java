package http;

import org.junit.Test;
import org.meizhuo.rpc.trace.TraceSendUtils;

public class HTTPTest {

    @Test
    public void testClientSend(){
        TraceSendUtils.clientSend(null);
    }

}
