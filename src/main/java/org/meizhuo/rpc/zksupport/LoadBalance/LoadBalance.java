package org.meizhuo.rpc.zksupport.LoadBalance;

import io.netty.channel.ChannelHandlerContext;

/**
 * Created by wephone on 18-1-8.
 * 负载均衡策略抽象接口
 * 其他模块不耦合负载均衡代码
 */
public interface LoadBalance {

    /**
     * 负载均衡的连接平衡操作
     * @param serviceName
     */
    void balance(String serviceName);

    /**
     * 负载均衡选择服务中已有的连接之一
     * @param serviceName
     * @return
     */
    ChannelHandlerContext chooseChannel(String serviceName);
}
