package org.meizhuo.rpc.client;

import io.netty.channel.ChannelHandlerContext;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by wephone on 18-1-8.
 * IP对应的channel类 用于一个IP映射的Map IPChannelMap
 * 存放一个IP对应的channel
 * serviceQuoteNum这个channel被服务引用的次数
 */
public class IPChannelInfo {

    private ChannelHandlerContext channelHandlerContext;
    //保证多线程修改时引用计数正确
    private AtomicInteger serviceQuoteNum;

    public ChannelHandlerContext getChannelHandlerContext() {
        return channelHandlerContext;
    }

    public void setChannelHandlerContext(ChannelHandlerContext channelHandlerContext) {
        this.channelHandlerContext = channelHandlerContext;
    }

    public Integer getServiceQuoteNum() {
        return serviceQuoteNum.get();
    }

    public int incrementServiceQuoteNum() {
        return serviceQuoteNum.incrementAndGet();
    }

    //服务不使用后 需要减去对channel的引用数量
    public int decrementServiceQuoteNum(){
        return serviceQuoteNum.decrementAndGet();
    }
}
