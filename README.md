# MeiZhuoRPC
**java远程调用框架**
**
Named By [MeiZhuo Studio](http://www.meizhuo.org/)
**


系列博客
- java打造RPC框架 
- http://blog.csdn.net/we_phone/article/details/78949331

### 用法 Usage
- 下载/out/artifacts/MeiZhuoRPC_jar里的jar包
- 将jar包添加进你的项目依赖
- Download the jar in /out/artifacts/MeiZhuoRPC_jar
- add MeiZhuoRPC.jar to your dependencies

#### 调用端 Consumer

**同步调用**
```
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"file:src/test/java/trace/simpleTrace/ClientContext.xml"})
public class Client {

    @Autowired
    ServiceInterface service;

    @Test
    public void start(){
        service.remoteService(233.0,"hhh");
        service.IntegerMethodTest(233);
        service.stringMethodIntegerArgsTest(233,666.66);
    }
}
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd">
    <bean class="org.meizhuo.rpc.client.ClientConfig">
        <property name="zooKeeperHost" value="127.0.0.1:2181"></property>
        <property name="serviceInterface">
            <map>
                <!--配置对应的服务ID及抽象接口全类名 key为服务的id，需和服务端保持一致-->
                <entry key="RPCService" value="trace.simpleTrace.ServiceInterface" ></entry>
            </map>
        </property>
        <property name="loadBalance" ref="Random"></property>
        <property name="overtime" value="5000"/>
    </bean>
    <bean scope="prototype" class="org.meizhuo.rpc.zksupport.LoadBalance.RandomBalance" id="Random"/>
</beans>
```
**流式异步调用**
```
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"file:src/test/java/trace/simpleTrace/ClientContext.xml"})
public class Client {

    @Autowired
    AsyncServerInterface asyncServer;

    @Test
    public void start(){
        asyncServer.remoteService(233.0,"hhh")
                        .then(new ThenCallBack<Double,Double>() {
                            @Override
                            public Double done(Double arg) {
                                asyncServer.IntegerMethodTest(2);
                                //不等待结果得再调用下一个异步rpc 例如日志记录等等
                                return arg;
                            }
                        })
                        .then(new NextCallBack<Double>() {
                            @Override
                            public Promise nextRPC(Double arg) {
                                //arg为上级调用的返回结果
                                System.out.println("double arg:"+arg);
                                //必须此异步调用返回时才会触发下一级then 用于多级异步rpc调用
                                return asyncServer.stringMethodIntegerArgsTest(123,arg);
                            }
                        })
                        .success(new SucessCallBack<String>() {
                            @Override
                            public void done(String result) {
                                System.out.println("string result:"+result);
                            }
                        })
                        .fail(new FailCallback() {
                            @Override
                            public void done(Exception e) {
                                e.printStackTrace();
                            }
                        });
    }
}
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd">
    <bean class="org.meizhuo.rpc.client.ClientConfig">
        <property name="zooKeeperHost" value="127.0.0.1:2181"></property>
        <property name="serviceInterface">
            <map>
                <!--配置对应的服务ID及抽象接口全类名-->
                <entry key="AsyncRPCService" value="trace.simpleTrace.AsyncServerInterface" ></entry>
            </map>
        </property>
        <property name="loadBalance" ref="Random"></property>
        <property name="overtime" value="5000"/>
    </bean>
    <bean scope="prototype" class="org.meizhuo.rpc.zksupport.LoadBalance.RandomBalance" id="Random"/>
</beans>
```
**异步调用回调**
- SuccessCallBack 所有异步rpc调用成功时调用
- FailCallback 失败时调用
- NextCallBack 下一级RPC调用，rpc调用返回时才会触发下一级then 泛型为上一级返回结果
- ThenCallBack 处理非需等待结果的RPC操作 或其他普通操作
- ThenVoidResCallBack 作用同ThenCallBack 但返回空结果给下一级

#### 实现端 Provider
```
/**
 *实现端代码及spring配置
 */
 public class Server {
 
     @Test
     public void multi1and2() throws InterruptedException, IOException {
         ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(
                 new String[] { "file:src/test/java/rpcTest/MultiServer1and2Context.xml" });
         context.start();
         //启动spring后才可启动 防止容器尚未加载完毕
         RPC.start();
     }
 }
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd">
    <bean class="org.meizhuo.rpc.server.ServerConfig">
            <property name="port" value="9999"></property>
            <property name="zooKeeperHost" value="127.0.0.1:2181"></property>
            <property name="serverImplMap">
                <map>
                    <!--配置对应的抽象接口及其实现-->
                    <entry key="RPCService" value="trace.simpleTrace.ServiceImpl"></entry>
                    <entry key="AsyncRPCService" value="trace.simpleTrace.ServiceImpl"></entry>
                </map>
            </property>
        </bean>
</beans>
```
#### 抽象接口及其实现
**异步接口抽象**
```
//必须注解async
@Async
public interface AsyncServerInterface {
    Promise remoteService(Double arg1, String arg2);
}
```
**同步接口抽象**
```
public interface ServiceInterface {
    Double remoteService(Double arg1, String arg2);
}
```
**接口实现**
```
public class ServiceImpl implements Service {
    //方法名参数都和异步接口一致时可调用 无需继承异步接口
    @Override
    public Double remoteService(Double arg1, String arg2) {
        return 1234567.0;
    }
}
```

#### zipkin链路追踪
```
<bean class="org.meizhuo.rpc.trace.TraceConfig">
    <!--配置每个进程的id 不可重复-->
    <property name="appId" value="1"/>
    <property name="zipkinUrl" value="127.0.0.1:9411"/>
</bean>
```

#### 负载均衡策略
配置在ClientConfig的loadBalance属性
config loadBalance attribute in ClientConfig
- 随机 RandomBalance
- 轮询 PollingBalance(尚未实现)
- 一致性hash ConsistentHashing(适用于调用者数量远大于提供者数量时，以减少开启不必要的长连接)

#### 协议
**Java协议（基于TCP的私有协议，不支持异构）**

协议头部

字段名 | 作用
---- | ---
length | 整包长度
traceIdLength |  链路追踪traceId长度
traceId |  链路追踪traceId
spanIdLength | 链路追踪spanId长度
spanId |  链路追踪spanId
requestIdLength |  请求Id长度
requestId | 请求Id
type |  1 RPC请求 2 RPC响应 3 RPC异常响应
time |  时间戳

协议体

字段名 | 作用
---- | ---
serviceLength | RPC服务Id字节长度
service |  RPC服务Id
methodLength | 调用方法名字节长度
method |  调用方法名
argsNum | 目标方法参数个数
args |  数组 参数列表
resultNameLength | 返回结果类名字节长度
resultName | 返回结果类名
resultLength | 返回内容字节长度
result | 字节数组 返回内容

**通用协议(尚未实现)**
- 支持异构语言
- 将采用tag-length-value模式
- 调用参数及返回结果仅支持基本类型与Map Set List 
- 欢迎熟悉其他语言的朋友一起参与