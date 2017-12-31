package org.meizhuo.rpc.client;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

import java.util.concurrent.locks.Condition;

/**
 * Created by wephone on 17-12-27.
 */
public class RPCRequestHandler extends ChannelHandlerAdapter {

    public static ChannelHandlerContext channelCtx;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
//        System.out.println("ChannelActive Thread:"+Thread.currentThread().getName());
        channelCtx=ctx;
        //需要在lock和unlock的包裹下 否则报出IllegalMonitorStateException
        RPCRequestNet.connectlock.lock();
        RPCRequestNet.connectCondition.signalAll();
        RPCRequestNet.connectlock.unlock();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        String requestID= (String) msg;
        synchronized (RPCRequestNet.requestLockMap.get(requestID)) {
            //唤醒在该对象锁上wait的线程
            RPCRequestNet.requestLockMap.get(requestID).notifyAll();
        }
    }
}
