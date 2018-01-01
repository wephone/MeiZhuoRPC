package rpcTest;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.meizhuo.rpc.core.RPC;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Created by wephone on 17-12-30.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"file:src/test/java/rpcTest/ClientContext.xml"})
public class Client {

    @Test
    public void start(){
        Service service= (Service) RPC.call(Service.class);
        System.out.println("RPC接收成功:"+service.remoteService(233.0,"hhh"));
        /**
         * 为了一定程度支持跨语言 没有直接用java原生的反序列化
         * 所以远程调用方法传参必须为包装类 int等基本类型在json转换之后再反序列化回来都会变成Integer等包装类 导致实现端找不到对应的方法 因为参数类型不匹配
         * java.lang.NoSuchMethodException: rpcTest.ServiceImpl.intMethodTest(java.lang.Integer)
         */
        try {
            System.out.println("测试int类型传参与返回:"+service.intMethodTest(666));
        }catch (NullPointerException e){
            System.out.println("远程调用方法传参必须为包装类");
        }
        System.out.println("测试Integer类型传参与int返回:"+service.intMethodIntegerArgsTest(777));
        System.out.println("测试Integer类型传参与返回:"+service.IntegerMethodTest(233));
        System.out.println("测试Integer,Double类型传参与返回String对象:"+service.stringMethodIntegerArgsTest(233,666.66));
    }

}
