package org.meizhuo.rpc.server;

import com.fasterxml.jackson.core.JsonProcessingException;
//import com.google.gson.Gson;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import org.meizhuo.rpc.client.RPCRequest;
import org.meizhuo.rpc.core.RPC;
import org.meizhuo.rpc.protocol.RPCProtocol;
import org.meizhuo.rpc.trace.SpanStruct;
import org.meizhuo.rpc.trace.TraceSendUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Date;

/**
 * Created by wephone on 17-12-30.
 */
public class RPCResponseHandler extends ChannelHandlerAdapter {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("Client Connect:"+ctx.channel().remoteAddress().toString());
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws IOException {
//        String requestJson= (String) msg;
        RPCProtocol requestProtocol= (RPCProtocol) msg;
//        System.out.println("receive request:"+requestJson);
        RPCRequest request= requestProtocol.buildRequestByProtocol();
        TraceSendUtils.serverReceived(request);
        RPCResponse response=new RPCResponse();
//        System.out.println("server response thread"+Thread.currentThread().getName());
        //从spring中取出bean进行调用 而不是直接反射
        Object result=InvokeServiceUtil.invoke(request);
        //目标方法调用之后埋点server response 而不是在目标方法前
        SpanStruct span=TraceSendUtils.preServerResponse(request,response);
        response.setRequestID(request.getRequestID());
        response.setResult(result);
//        String respStr=RPC.responseEncode(response);
//        ByteBuf responseBuf= Unpooled.copiedBuffer(respStr.getBytes());
        RPCProtocol rpcResponseProtocol=RPC.getServerConfig().getRPCProtocol();
        response.setResponseTime(System.currentTimeMillis());
        rpcResponseProtocol.buildResponseProtocol(response);
        ctx.writeAndFlush(rpcResponseProtocol);
        TraceSendUtils.serverResponse(span);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();//这句的作用是将消息发送队列中的消息写入到SocketChannel中发给对方
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }

}
