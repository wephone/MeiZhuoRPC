package trace.serverMultiTrace;

import org.junit.Test;
import org.meizhuo.rpc.core.RPC;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.IOException;

public class Factory {

    @Test
    public void factoryStart() throws InterruptedException, IOException {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(
                new String[] { "file:src/test/java/trace/serverMultiTrace/FactoryContext.xml" });
        context.start();
        //启动spring后才可启动 防止容器尚未加载完毕
        RPC.start();
    }

}
