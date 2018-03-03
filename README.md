# MeiZhuoRPC
**java远程调用框架**
**By MeiZhuo Studio**
**www.meizhuo.org**

系列博客
- java打造RPC框架 
- http://blog.csdn.net/we_phone/article/details/78949331

### 用法 Usage
- 下载/out/artifacts/MeiZhuoRPC_jar里的jar包
- 将jar包添加进你的项目依赖
- Download the jar in /out/artifacts/MeiZhuoRPC_jar
- add MeiZhuoRPC.jar to your dependencies

#### 调用端 Consumer
```
/**
 *调用端代码及spring配置
 *调用方法形参不可为int等基本类型，需为Integer等包装类
 */
    @Test
    public void start(){
        CountDownLatch countDownLatch=new CountDownLatch(1);
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(
                new String[] { "file:src/test/java/rpcTest/MultiServiceClientContext.xml" });
        context.start();
        Service1 service1= (Service1) RPC.call(Service1.class);
        Service2 service2= (Service2) RPC.call(Service2.class);
        service1.testVoid();
        service1.testVoid("void args");
        service1.testStringVoid("发送");
        String res=service2.testString(2.3);
        System.out.println(res);
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd">
    <bean class="org.meizhuo.rpc.client.ClientConfig">
        <property name="zooKeeperHost" value="127.0.0.1:2181"></property>
        <property name="serviceInterface">
            <set>
                <value>rpcTest.Service1</value>
                <value>rpcTest.Service2</value>
            </set>
        </property>
        <!--负载均衡策略-->
        <property name="loadBalance" ref="Random"></property>
    </bean>
    <bean scope="prototype" class="org.meizhuo.rpc.zksupport.LoadBalance.RandomBalance" id="Random">
    </bean>
</beans>
```
#### 实现端 Provider
```
/**
 *实现端代码及spring配置
 */
 @RunWith(SpringJUnit4ClassRunner.class)
 @ContextConfiguration(locations={"file:src/test/java/rpcTest/ServerContext.xml"})
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
        <property name="port" value="9012"></property>
        <property name="zooKeeperHost" value="127.0.0.1:2181"></property>
        <property name="serverImplMap">
            <map>
                <!--配置对应的抽象接口及其实现-->
                <entry key="rpcTest.Service1" value="rpcTest.Service1Impl"></entry>
                <entry key="rpcTest.Service2" value="rpcTest.Service2Impl"></entry>
            </map>
        </property>
    </bean>
</beans>
```

#### 负载均衡策略
配置在ClientConfig的loadBalance属性
config loadBalance attribute in ClientConfig
- 随机 RandomBalance
- 轮询 PollingBalance(尚未实现)
- 一致性hash ConsistentHashing(适用于调用者数量远大于提供者数量时，以减少开启不必要的长连接)

#### 抽象及其实现 Service and implementation
```
/**
 *调用方法形参不可为int等基本类型，需为Integer等包装类
 */
public interface Service1 {
    void testVoid();
    void testVoid(String voidarg);
    void testStringVoid(String a);
    Integer count();
}
public class Service1Impl implements Service1 {

    private static AtomicInteger count=new AtomicInteger(0);

    @Override
    public void testVoid() {
        System.out.println("完全空的RPC调用");
    }

    @Override
    public void testVoid(String voidarg) {
        System.out.println("测试同名不同参数的方法:"+voidarg);
    }

    @Override
    public void testStringVoid(String a) {
        System.out.println("接收到字符:"+a);
    }

    @Override
    public Integer count() {
        Integer res=count.addAndGet(1);
        System.out.println("Service1 计数:"+res);
        return res;
    }
}
```
## Branch master
- zookeeper单点注册中心基本完成
- zookeeper集群注册中心配置持续更新中...
- finish the support for single zookeeper registry center
- zookeeper cluster registry center is building

## Version/Branch1.0
- 仅一对一RPC调用 
- only one-on-one Remote Procedure Call 

### 实现思路
- 实现端spring配置全局的map 维护抽象接口与实现的映射关系
- Server/Client对应的Config配置类利用ApplicationContextAware接口 获取当前的spring容器
- 调用端动态代理要使用的抽象接口 使调用端使用接口方法时忽略底层细节
- 网络IO采用Netty框架实现 封装RPCRequest，RPCResponse类作为发送和返回信息的Bean
- rpc默认是同步调用 全局维护一个map 映射请求ID与对应线程生成的RPCRequest对象
- 使用对象的RPCRequest对象作为对象锁 同步等待(设置超时时间)连接成功或调用异步返回时notify
- 代理回调handler里把对象的接口信息(方法名,参数等)封装在RPCRequest里通过网络IO发给实现端
- 为尽量支持跨语言，不采用java原生序列化 RPCRequest，RPCResponse类传输过程中的编解码使用json格式，protobuf等不支持Object类型，必须强类型 会降低使用的灵活性
- 采用jackson处理json Gson会将int/long默认转为double 所以不采用
- 实现端接受到参数，在map里找到对应的实现类的信息 反射调用相应类的方法
- 远程调用方法形参必须为Integer等包装类，Json反序列话为对象后，object数组内类型全部会转化为包装类 会导致基本类型int的等方法类型不匹配 无法调用
- 调用端把返回值解码后把结果传输回实现端
- 接入zookeeper注册中心后 使用watcher机制监听调用者和提供者的节点变化
- 根据提供者,调用者的数量进行相应的负载均衡
