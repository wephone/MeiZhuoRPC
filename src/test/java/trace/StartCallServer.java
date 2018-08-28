package trace;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"file:src/test/java/trace/FirstServerContext.xml"})
public class StartCallServer {

    @Autowired
    StartCall startCall;

    @Test
    public void RPC(){
        System.out.println("发起调用");
        System.out.println(startCall.callSecond());
    }

}
