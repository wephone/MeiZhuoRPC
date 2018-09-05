package org.meizhuo.rpc.trace;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpVersion;
import org.meizhuo.rpc.client.RPCRequest;
import org.meizhuo.rpc.core.RPC;
import org.meizhuo.rpc.promise.Deferred;
import org.meizhuo.rpc.protocol.IdUtils;
import org.meizhuo.rpc.server.RPCResponse;
import org.meizhuo.rpc.threadLocal.TraceThreadLocal;

import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class TraceSendUtils {

    private static HTTPConnectionPool httpPool=new HTTPConnectionPool();
    private static String uri="/api/v2/spans";
    private static ObjectMapper objectMapper=new ObjectMapper();
    private static ThreadPoolExecutor postSpanExecutor=new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors()*2,Runtime.getRuntime().availableProcessors()*3,15, TimeUnit.SECONDS,
            new LinkedBlockingDeque<>(),
            new NamedThreadFactory("PostSpan"),
            new ThreadPoolExecutor.DiscardPolicy());
    private static final String serverSuffix="-SERVER";
    private static final String clientSuffix="-CLIENT";


    private static void zipKinHTTPSend(SpanStruct span){
        String spanJson="";
        try {
            spanJson=objectMapper.writeValueAsString(span);
            spanJson="["+spanJson+"]";
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        DefaultFullHttpRequest request = new DefaultFullHttpRequest(
                HttpVersion.HTTP_1_1, HttpMethod.POST, uri,Unpooled.wrappedBuffer(spanJson.getBytes()));
        String zipkinUrl=RPC.getTraceConfig().getZipkinUrl().split(";")[0];
        request.headers().set(HttpHeaders.Names.HOST,zipkinUrl);
        request.headers().set(HttpHeaders.Names.CONTENT_TYPE,
                "application/json;charset=UTF-8");
        request.headers().set(HttpHeaders.Names.CONNECTION,
                HttpHeaders.Names.CONNECTION);
        request.headers().set(HttpHeaders.Names.CONTENT_LENGTH, request.content().readableBytes());
        try {
            Channel channel=httpPool.getHTTPChannel();
            channel.writeAndFlush(request);
            httpPool.releaseHTTPChannel(channel);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 同步调用前检查当前线程threadLocal
     * 将这部分信息存在request中
     * @param rpcRequest
     * @return
     */
    public static SpanStruct preClientSend(RPCRequest rpcRequest){
        if (RPC.isTrace()) {
            //查看threadLocal是否有链路信息
            SpanStruct spanInThread = TraceThreadLocal.getSpanInThread();
            String traceId=null;
            String parentSpanId=null;
            if (spanInThread!=null) {
                traceId= spanInThread.getTraceId();
                parentSpanId = spanInThread.getParentId();
            }
            String spanId = IdUtils.getSpanId();
            SpanStruct span = new SpanStruct();
            if (traceId == null) {
                traceId = IdUtils.getTraceId();
            }
            span.setTraceId(traceId);
            span.setId(spanId);
            if (parentSpanId != null) {
                span.setParentId(parentSpanId);
            }
            //存到request中发送
            rpcRequest.setTraceId(traceId);
            rpcRequest.setSpanId(spanId);
            span.setKind(SpanStruct.CLIENT_KIND);
            span.setName(rpcRequest.getMethodName());
            SpanStruct.LocalEndpoint localEndpoint=span.new LocalEndpoint();
            localEndpoint.setServiceName(rpcRequest.getMethodName()+clientSuffix);
            span.setLocalEndpoint(localEndpoint);
            return span;
        }
        return null;
    }

    public static void clientSend(SpanStruct span){
        if (RPC.isTrace()) {
            SpanStruct spanInThread = TraceThreadLocal.getSpanInThread();
            Long lastTime = null;
            if (spanInThread!=null){
                lastTime = spanInThread.getTimestamp();
            }
            Long now = System.currentTimeMillis();
            span.setTimestamp(now * 1000);
            if (lastTime != null) {
                span.setDuration(now*1000 - lastTime);
            }
            postSpanExecutor.submit(new Runnable() {
                @Override
                public void run() {
                    zipKinHTTPSend(span);
                }
            });
        }
    }

    public static void clientReceived(RPCResponse rpcResponse,RPCRequest rpcRequest){
        if (RPC.isTrace()) {
            SpanStruct span = new SpanStruct();
            Long now = System.currentTimeMillis();
            span.setTimestamp(now*1000);
            span.setTraceId(rpcResponse.getTraceId());
            span.setParentId(rpcResponse.getSpanId());
            span.setName(rpcRequest.getMethodName());
            String spanId = IdUtils.getSpanId();
            span.setId(spanId);
            //将链路信息存到threadLocal
            SpanStruct spanInThread = new SpanStruct();
            spanInThread.setParentId(spanId);
            spanInThread.setTraceId(rpcResponse.getTraceId());
            //存入thread的都*1000
            spanInThread.setTimestamp(now*1000);
            TraceThreadLocal.setSpanInThread(spanInThread);
            postSpanExecutor.submit(new Runnable() {
                @Override
                public void run() {
                    span.setDuration((now - rpcResponse.getResponseTime()) * 1000);
                    span.setKind(SpanStruct.CLIENT_KIND);
                    SpanStruct.LocalEndpoint localEndpoint=span.new LocalEndpoint();
                    localEndpoint.setServiceName(rpcRequest.getMethodName()+clientSuffix);
                    span.setLocalEndpoint(localEndpoint);
                    zipKinHTTPSend(span);
                }
            });
        }
    }

    public static void serverReceived(RPCRequest rpcRequest){
        if (RPC.isTrace()) {
            SpanStruct span = new SpanStruct();
            SpanStruct spanInThread = new SpanStruct();
            Long now = System.currentTimeMillis();
            String spanId = IdUtils.getSpanId();
            spanInThread.setTraceId(rpcRequest.getTraceId());
            spanInThread.setParentId(spanId);
            spanInThread.setTimestamp(now * 1000);
            //设置链路信息在threadLocal
            TraceThreadLocal.setSpanInThread(spanInThread);
            postSpanExecutor.submit(new Runnable() {
                @Override
                public void run() {
                    span.setId(spanId);
                    span.setTraceId(rpcRequest.getTraceId());
                    span.setParentId(rpcRequest.getSpanId());
                    span.setName(rpcRequest.getMethodName());
                    span.setKind(SpanStruct.SERVER_KIND);
                    span.setTimestamp(now * 1000);
                    Long duration = now - rpcRequest.getRequestTime();
                    span.setDuration(duration * 1000);
                    String localIp = RPC.getServerConfig().getServerHost();
                    SpanStruct.LocalEndpoint localEndpoint = span.new LocalEndpoint();
                    localEndpoint.setIpv4(localIp);
                    localEndpoint.setServiceName(rpcRequest.getMethodName()+serverSuffix);
                    span.setLocalEndpoint(localEndpoint);
                    zipKinHTTPSend(span);
                }
            });
        }
    }

    /**
     * 执行对应RPC实现方法前调用 用于封装response span 设置当前线程链路信息
     * @param rpcRequest
     * @param rpcResponse
     * @return
     */
    public static SpanStruct preServerResponse(RPCRequest rpcRequest,RPCResponse rpcResponse){
        if (RPC.isTrace()) {
            SpanStruct span = new SpanStruct();
            String spanId = IdUtils.getSpanId();
            SpanStruct spanInThread = TraceThreadLocal.getSpanInThread();
            //server response前肯定有接受的链路 所以spanInThread肯定不能空
//            if (spanInThread!=null) {
//                spanInThread.setParentId(spanId);
//            }else {
//                spanInThread=new SpanStruct();
//                spanInThread.setTraceId(rpcRequest.getTraceId());
//                spanInThread.setParentId(spanId);
//            }
//            TraceThreadLocal.setSpanInThread(spanInThread);
            span.setId(spanId);
            span.setTraceId(spanInThread.getTraceId());
            //threadLocal中取出上级调用的链路信息
            span.setParentId(spanInThread.getParentId());
            span.setName(rpcRequest.getMethodName());
            span.setKind(SpanStruct.SERVER_KIND);
            String localIp = RPC.getServerConfig().getServerHost();
            SpanStruct.LocalEndpoint localEndpoint = span.new LocalEndpoint();
            localEndpoint.setServiceName(rpcRequest.getMethodName()+serverSuffix);
            localEndpoint.setIpv4(localIp);
            span.setLocalEndpoint(localEndpoint);
            rpcResponse.setTraceId(rpcRequest.getTraceId());
            rpcResponse.setSpanId(spanId);
            return span;
        }
        return null;
    }

    public static void serverResponse(SpanStruct span){
        if (RPC.isTrace()) {
            Long now = System.currentTimeMillis();
            SpanStruct spanInThread = TraceThreadLocal.getSpanInThread();
            span.setDuration(now * 1000 - spanInThread.getTimestamp());
            span.setTimestamp(now * 1000);
            spanInThread.setParentId(span.getId());
            TraceThreadLocal.setSpanInThread(spanInThread);
            //无需再处理threadLocal中的链路信息
            postSpanExecutor.submit(new Runnable() {
                @Override
                public void run() {
                    zipKinHTTPSend(span);
                }
            });
        }
    }

    /**
     * 异步发送前在当前线程内的准备工作
     * 取出promise中的链路信息 修改parentId后归还promise给下级调用 返回span在发送时拼接信息
     * @param promise
     * @return
     */
    public static SpanStruct preClientAsyncSend(Deferred promise){
        if (RPC.isTrace()) {
            SpanStruct span = new SpanStruct();
            SpanStruct spanInThread = TraceThreadLocal.getSpanInThread();
            //取出trace和parentSpan 不存在则查找threadLocal 否则生成
            String traceId = promise.getTraceId();
            if (traceId == null) {
                if (spanInThread==null){
                    traceId = IdUtils.getTraceId();
                }else {
                    traceId = spanInThread.getTraceId();
                }
            }
            promise.setTraceId(traceId);
            span.setTraceId(traceId);
            String parentSpanId = promise.getParentSpanId();
            if (parentSpanId != null) {
                span.setParentId(parentSpanId);
            } else {
                if (spanInThread != null) {
                    span.setParentId(spanInThread.getParentId());
                }
            }
            //生成本次的spanId传递给promise 交给下一级调用
            String spanId = IdUtils.getSpanId();
            span.setId(spanId);
            promise.setParentSpanId(spanId);
            //将链路信息存入threadLocal
            if (spanInThread==null){
                spanInThread=new SpanStruct();
            }
            spanInThread.setTraceId(traceId);
            spanInThread.setParentId(spanId);
            TraceThreadLocal.setSpanInThread(spanInThread);
            SpanStruct.LocalEndpoint localEndpoint=span.new LocalEndpoint();
            localEndpoint.setServiceName(promise.getMethodName()+clientSuffix);
            span.setName(promise.getMethodName());
            span.setLocalEndpoint(localEndpoint);
            return span;
        }
        return null;
    }

    public static void clientAsyncSend(SpanStruct span){
        if (RPC.isTrace()) {
            Long now=System.currentTimeMillis();
            SpanStruct spanInThread=TraceThreadLocal.getSpanInThread();
            if (spanInThread.getTimestamp()!=null){
                span.setDuration(now*1000-spanInThread.getTimestamp());
            }else {
                span.setDuration(0L);
            }
            spanInThread.setTimestamp(now*1000);
            TraceThreadLocal.setSpanInThread(spanInThread);
            postSpanExecutor.submit(new Runnable() {
                @Override
                public void run() {
                    span.setKind(SpanStruct.CLIENT_KIND);
                    //单位是微秒
                    span.setTimestamp(System.currentTimeMillis()*1000);
                    zipKinHTTPSend(span);
                }
            });
        }
    }

    public static void clientAsyncReceived(RPCResponse rpcResponse,Deferred promise){
        if (RPC.isTrace()) {
            //异步返回接受无需处理线程内链路信息
            SpanStruct span = new SpanStruct();
            String traceId=rpcResponse.getTraceId();
            String parentSpanId=rpcResponse.getSpanId();
            promise.setParentSpanId(parentSpanId);
            Long now=System.currentTimeMillis();
            Long duration=now-rpcResponse.getResponseTime();
            span.setDuration(duration*1000);
            String methodName=promise.getMethodName();
            //方法异步调用结束 弹出栈 必须同步完成 异步会导致链路紊乱
            promise.finishMethod();
            postSpanExecutor.submit(new Runnable() {
                @Override
                public void run() {
                    span.setTraceId(traceId);
                    span.setParentId(parentSpanId);
                    span.setId(IdUtils.getSpanId());
                    span.setName(methodName);
                    span.setKind(SpanStruct.CLIENT_KIND);
                    //单位是微秒
                    span.setTimestamp(now*1000);
                    SpanStruct.LocalEndpoint localEndpoint=span.new LocalEndpoint();
                    localEndpoint.setServiceName(methodName+clientSuffix);
                    span.setLocalEndpoint(localEndpoint);
                    zipKinHTTPSend(span);
                }
            });

        }
    }

//    private static void buildAsyncClientSendTrace(Deferred promise,SpanStruct span){
//        String traceId;
//        String parentSpanId=null;
//        //promise中不存在 则取threadLocal的tracee
//        //threadLocal的不存在则新建一个trace
//        if (promise.getTraceId()==null){
//            String traceInThread=TraceThreadLocal.getTraceIdInThread();
//            if (traceInThread==null) {
//                traceId = IdUtils.getTraceId();
//                promise.setTraceId(traceId);
//            }else {
//                traceId=traceInThread;
//            }
//        }else {
//            traceId=promise.getTraceId();
//        }
//        if (promise.getParentSpanId()==null){
//            String spanInThread=TraceThreadLocal.getParentSpanIdInThread();
//            if (spanInThread!=null) {
//                parentSpanId=spanInThread;
//            }else {
//                promise.setParentSpanId(parentSpanId);
//            }
//        }else {
//            parentSpanId=promise.getParentSpanId();
//        }
//        if (parentSpanId!=null){
//            span.setParentId(parentSpanId);
//        }
//        span.setTraceId(traceId);
//    }



}
