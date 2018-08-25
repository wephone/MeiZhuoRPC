package org.meizhuo.rpc.protocol;

import java.util.concurrent.atomic.AtomicLong;

public class IdUtils {

    private static AtomicLong traceCount=new AtomicLong();
    private static AtomicLong spanCount=new AtomicLong();
    private static AtomicLong requestCount=new AtomicLong();

    public static String getTraceId(){
        return "";
    }

    public static String getSpanId(){
        return "";
    }

    public static String getRequestId(){
        return "";
    }

}
