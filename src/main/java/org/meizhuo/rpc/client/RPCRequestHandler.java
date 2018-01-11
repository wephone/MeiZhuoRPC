package org.meizhuo.rpc.client;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import org.meizhuo.rpc.core.RPC;
import org.meizhuo.rpc.server.RPCResponse;

import java.util.concurrent.locks.Condition;

/**
 * Created by wephone on 17-12-27.
 */
public class RPCRequestHandler extends ChannelHandlerAdapter {

//    public static ChannelHandlerContext channelCtx;

//    @Override
//    public void channelActive(ChannelHandlerContext ctx) throws Exception {
////        System.out.println("ChannelActive Thread:"+Thread.currentThread().getName());
//        channelCtx=ctx;
//        //需要在lock和unlock的包裹下 否则报出IllegalMonitorStateException
//        RPCRequestNet.getInstance().connectlock.lock();
//        RPCRequestNet.getInstance().connectCondition.signalAll();
//        RPCRequestNet.getInstance().connectlock.unlock();
//    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        String responseJson= (String) msg;
        RPCResponse response= (RPCResponse) RPC.responseDecode(responseJson);
        synchronized (RPCRequestNet.getInstance().requestLockMap.get(response.getRequestID())) {
            //唤醒在该对象锁上wait的线程
            RPCRequest request= (RPCRequest) RPCRequestNet.getInstance().requestLockMap.get(response.getRequestID());
            request.setResult(response.getResult());
            request.notifyAll();
        }
    }
}
