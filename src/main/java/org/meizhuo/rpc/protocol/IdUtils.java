package org.meizhuo.rpc.protocol;

import org.meizhuo.rpc.core.RPC;

import java.util.concurrent.atomic.AtomicLong;

public class IdUtils {

    private static AtomicLong traceCount=new AtomicLong();
    private static AtomicLong spanCount=new AtomicLong();
    private static AtomicLong requestCount=new AtomicLong();

    /**
     * 时间戳转16进制+appId+atomicLong转16进制
     * 分布式全局唯一
     */
    public static String getTraceId(){
        if (RPC.isTrace()) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(Long.toUnsignedString(System.currentTimeMillis(), 16));
            stringBuilder.append(RPC.getTraceConfig().getAppId());
            stringBuilder.append(Long.toUnsignedString(traceCount.getAndIncrement(), 16));
            return stringBuilder.toString();
        }
        return "";
    }

    /**
     * 时间戳转16进制+appId+atomicLong转16进制
     */
    public static String getSpanId(){
        if (RPC.isTrace()) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(Long.toUnsignedString(System.currentTimeMillis(), 16));
            stringBuilder.append(RPC.getTraceConfig().getAppId());
            stringBuilder.append(Long.toUnsignedString(spanCount.getAndIncrement(), 16));
            return stringBuilder.toString();
        }
        return "";
    }

    /**
     * 时间戳转32进制+atomicLong转32进制
     * 无需考虑跨进程冲突 跨进程不在同一个channel内
     */
    public static String getRequestId(){
        StringBuilder stringBuilder=new StringBuilder();
        stringBuilder.append(Long.toUnsignedString(System.currentTimeMillis(),16));
        stringBuilder.append(Long.toUnsignedString(requestCount.getAndIncrement(),16));
        return stringBuilder.toString();
    }

//    public static void main(String[] arg){
//        String data= Long.toUnsignedString(Long.MAX_VALUE,16);
//        System.out.println(data);
//        String data1= Long.toUnsignedString(Long.MIN_VALUE,16);
//        System.out.println(data1);
//        String time=Long.toUnsignedString(33092508652000L,16);
//        System.out.println((time+data).length());
//        time=Long.toUnsignedString(System.currentTimeMillis(),16);
//        String data3= Long.toUnsignedString(0,16);
//        System.out.println((time+data3).length());
//    }
}
