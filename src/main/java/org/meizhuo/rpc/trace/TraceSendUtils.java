package org.meizhuo.rpc.trace;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpVersion;
import org.meizhuo.rpc.core.RPC;
import org.meizhuo.rpc.promise.Deferred;
import org.meizhuo.rpc.protocol.IdUtils;
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

    private static void sendToZipkin(SpanStruct span){
        postSpanExecutor.submit(new Runnable() {
            @Override
            public void run() {
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
        });
    }

    public static void clientSend(){
        SpanStruct span=new SpanStruct();
        sendToZipkin(span);
    }

    public static void clientReceived(){
        SpanStruct span=new SpanStruct();
        sendToZipkin(span);
    }

    public static void serverReceived(){
        SpanStruct span=new SpanStruct();
        sendToZipkin(span);
    }

    public static void serverResponse(){
        SpanStruct span=new SpanStruct();
        sendToZipkin(span);
    }

    public static void clientAsyncSend(Deferred promise){
        if (RPC.isTrace()) {
            SpanStruct span = new SpanStruct();
            buildTraceSpanIdByPromise(promise,span);
            sendToZipkin(span);
        }
    }

    public static void clientAsyncReceived(Deferred promise){
        if (RPC.isTrace()) {
            SpanStruct span = new SpanStruct();
            buildTraceSpanIdByPromise(promise,span);
            sendToZipkin(span);
        }
    }

    private static void buildTraceSpanIdByPromise(Deferred promise,SpanStruct span){
        String traceId;
        String parentSpanId=null;
        //promise中不存在 则取threadLocal的tracee
        //threadLocal的不存在则新建一个trace
        if (promise.getTraceId()==null){
            String traceInThread=TraceThreadLocal.getTraceIdInThread();
            if (traceInThread==null) {
                traceId = IdUtils.getTraceId();
                promise.setTraceId(traceId);
            }else {
                traceId=traceInThread;
            }
        }else {
            traceId=promise.getTraceId();
        }
        if (promise.getParentSpanId()==null){
            String spanInThread=TraceThreadLocal.getParentSpanIdInThread();
            if (spanInThread!=null) {
                parentSpanId=spanInThread;
            }
        }else {
            parentSpanId=promise.getParentSpanId();
        }
        if (parentSpanId!=null){
            span.setParentId(parentSpanId);
        }
        span.setTraceId(traceId);
    }

}
