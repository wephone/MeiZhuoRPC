package asyncRpcTest;

import org.meizhuo.rpc.promise.Promise;

public class FunctionImpl {

    /**
     * get str args:first args
     * get int args:23333
     * get str args:remote string callback
     */

    private static int count=0;

    public Integer getInteger(String a){
        System.out.println("get str args:"+a);
        return 23333+count++;
    }

    //这里不再返回promise所以不能直接实现那个接口 XML配置里指定即可
    //注意修饰符要public 不然报错can not access a member of class asyncRpcTest.FunctionImpl with modifiers ""
    public String getString(Integer a){
        System.out.println("get int args:"+a);
        return "remote string callback";
    }

}
