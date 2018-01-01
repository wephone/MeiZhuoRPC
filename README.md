# MeiZhuoRPC
**java远程调用框架**
**[By MeiZhuo Studio][www.meizhuo.org]**
## Version/Branch1.0
### Usage
- Download the jar in /out/artifacts/MeiZhuoRPC_jar
- add MeiZhuoRPC.jar to your classpath
#### Client调用端
```
/**
 *调用端代码及spring配置
 *调用方法形参不可为int等基本类型，需为Integer等包装类
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"file:src/test/java/rpcTest/ClientContext.xml"})
public class Client {

    @Test
    public void start(){
        Service service= (Service) RPC.call(Service.class);
        System.out.println("测试Integer,Double类型传参与返回String对象:"+service.stringMethodIntegerArgsTest(233,666.66));
    }

}
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd">
    <bean class="org.meizhuo.rpc.client.ClientConfig">
        <property name="host" value="127.0.0.1"></property>
        <property name="port" value="9999"></property>
    </bean>
</beans>
```
#### Server实现端
```
/**
 *实现端代码及spring配置
 */
 @RunWith(SpringJUnit4ClassRunner.class)
 @ContextConfiguration(locations={"file:src/test/java/rpcTest/ServerContext.xml"})
 public class Server {
 
     @Test
     public void start(){
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
         <property name="serverImplMap">
             <map>
                 <!--配置对应的抽象接口及其实现-->
                 <entry key="rpcTest.Service" value="rpcTest.ServiceImpl"></entry>
             </map>
         </property>
     </bean>
 </beans>
```
#### Service抽象及其实现
```
/**
 *调用方法形参不可为int等基本类型，需为Integer等包装类
 */
public interface Service {
    String stringMethodIntegerArgsTest(Integer a,Double b);
}
public class ServiceImpl implements Service {
    @Override
    public String stringMethodIntegerArgsTest(Integer a, Double b) {
        return "String"+a+b;
    }
}
```
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
