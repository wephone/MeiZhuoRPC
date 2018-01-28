package org.meizhuo.rpc.zksupport.LoadBalance;

import org.meizhuo.rpc.Exception.ProvidersNoFoundException;

import java.util.List;

/**
 * Created by wephone on 18-1-8.
 * 负载均衡策略抽象接口
 * 其他模块不耦合负载均衡代码
 */
public interface LoadBalance {

    /**
     * 负载均衡选择服务中已选中的IP之一
     * @param serviceName
     * @return
     */
    String chooseIP(String serviceName) throws ProvidersNoFoundException;

    /**
     * ip发生改变时回调此方法
     * @param serviceName
     * @param newIP
     */
    void changeIP(String serviceName,List<String> newIP);
}
