package org.meizhuo.rpc.threadLocal;

import com.alibaba.ttl.TransmittableThreadLocal;
import org.meizhuo.rpc.trace.SpanStruct;

public class TraceThreadLocal {

    private static TransmittableThreadLocal<SpanStruct> spanThreadLocal=new TransmittableThreadLocal<SpanStruct>();

    public static SpanStruct getSpanInThread(){
        return spanThreadLocal.get();
    }

    public static void setSpanInThread(SpanStruct span){
        spanThreadLocal.set(span);
    }

}
