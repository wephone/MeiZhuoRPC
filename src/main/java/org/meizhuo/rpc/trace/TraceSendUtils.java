package org.meizhuo.rpc.trace;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.buffer.Unpooled;
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


    private static void zipKinHTTPSend(SpanStruct span){
        String spanJson="";
        try {
            spanJson=objectMapper.writeValueAsString(span);
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
            httpPool.getHTTPChannel().writeAndFlush(request);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void clientSend(){
        SpanStruct span=new SpanStruct();
//        sendToZipkin(span);
    }

    public static void clientReceived(){
        SpanStruct span=new SpanStruct();
//        sendToZipkin(span);
    }

    public static void serverReceived(RPCRequest rpcRequest){
        SpanStruct span=new SpanStruct();
        postSpanExecutor.submit(new Runnable() {
            @Override
            public void run() {
                span.setId(IdUtils.getSpanId());
                span.setTraceId(rpcRequest.getTraceId());
                span.setParentId(rpcRequest.getSpanId());
                span.setName(rpcRequest.getServiceId());
                span.setKind(SpanStruct.SERVER_KIND);
                span.setTimestamp(System.currentTimeMillis()*1000);
                Long duration=System.currentTimeMillis()-rpcRequest.getRequestTime();
                span.setDuration(duration*1000);
                String localIp=RPC.getServerConfig().getServerHost();
                SpanStruct.LocalEndpoint localEndpoint=span.new LocalEndpoint();
                localEndpoint.setIpv4(localIp);
                span.setLocalEndpoint(localEndpoint);
            }
        });
    }

    public static void serverResponse(RPCRequest rpcRequest){
        SpanStruct span=new SpanStruct();
        postSpanExecutor.submit(new Runnable() {
            @Override
            public void run() {
                span.setId(IdUtils.getSpanId());
                span.setTraceId(rpcRequest.getTraceId());
                span.setParentId(rpcRequest.getSpanId());
                span.setName(rpcRequest.getServiceId());
                span.setKind(SpanStruct.SERVER_KIND);
                span.setTimestamp(System.currentTimeMillis()*1000);
                String localIp=RPC.getServerConfig().getServerHost();
                SpanStruct.LocalEndpoint localEndpoint=span.new LocalEndpoint();
                localEndpoint.setIpv4(localIp);
                span.setLocalEndpoint(localEndpoint);
            }
        });
    }
    
    public static void clientAsyncSend(SpanStruct span){
        if (RPC.isTrace()) {
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
            SpanStruct span = new SpanStruct();
            String traceId=rpcResponse.getTraceId();
            String parentSpanId=rpcResponse.getSpanId();
            promise.setParentSpanId(parentSpanId);
            Long duration=System.currentTimeMillis()-rpcResponse.getReceivedTime();
            span.setDuration(duration*1000);
            postSpanExecutor.submit(new Runnable() {
                @Override
                public void run() {
                    span.setTraceId(traceId);
                    span.setParentId(parentSpanId);
                    span.setId(IdUtils.getSpanId());
                    span.setName(rpcResponse.getServiceId());
                    span.setKind(SpanStruct.CLIENT_KIND);
                    //单位是微秒
                    span.setTimestamp(System.currentTimeMillis()*1000);
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

    /**
     * 取出promise中的链路信息 修改parentId后归还promise给下级调用 返回span在发送时拼接信息
     * @param promise
     * @return
     */
    public static SpanStruct getPromiseTraceInfo(Deferred promise){
        SpanStruct span=new SpanStruct();
        //取出trace和parentSpan 不存在trace则生成
        String traceId=promise.getTraceId();
        if (traceId==null){
            traceId=IdUtils.getTraceId();
            promise.setTraceId(traceId);
        }
        span.setTraceId(traceId);
        String parentSpanId=promise.getParentSpanId();
        if (parentSpanId!=null){
            span.setParentId(parentSpanId);
        }
        //生成本次的spanId传递给promise 交给下一级调用
        String spanId=IdUtils.getSpanId();
        span.setId(spanId);
        promise.setParentSpanId(spanId);
        return span;
    }

}
