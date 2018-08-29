package org.meizhuo.rpc.threadLocal;

import com.alibaba.ttl.TransmittableThreadLocal;

public class TraceThreadLocal {

    private static TransmittableThreadLocal<String> traceIdThreadLocal=new TransmittableThreadLocal<String>();
    private static TransmittableThreadLocal<String> spanIdThreadLocal=new TransmittableThreadLocal<String>();

    public static String getTraceIdInThread(){
        return traceIdThreadLocal.get();
    }

    public static String getParentSpanIdInThread(){
        return spanIdThreadLocal.get();
    }

    public static void setTraceIdInThread(String traceId){
        traceIdThreadLocal.set(traceId);
    }

    public static void setParentSpanIdInThread(String spanId){
        spanIdThreadLocal.set(spanId);
    }

}
