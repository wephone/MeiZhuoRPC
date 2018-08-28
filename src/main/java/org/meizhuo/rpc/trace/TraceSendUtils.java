package org.meizhuo.rpc.trace;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpVersion;
import org.meizhuo.rpc.core.RPC;

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

}
