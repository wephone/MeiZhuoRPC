package org.meizhuo.rpc.protocol;

public class Header {

    /**
     * 预留用于链路追踪
     */
    private Long traceId;
    /**
     * 每次RPC调用对应一个requestId
     */
    private Long requestId;
    /**
     * 调用类型 1:RPC请求 2:RPC响应 2:异常RPC响应
     */
    private Byte type;

    public final static Byte T_REQ=1;
    public final static Byte T_RESP=2;
    public final static Byte T_EX_RESP=3;

    public Long getTraceId() {
        return traceId;
    }

    public void setTraceId(Long traceId) {
        this.traceId = traceId;
    }

    public Long getRequestId() {
        return requestId;
    }

    public void setRequestId(Long requestId) {
        this.requestId = requestId;
    }

    public Byte getType() {
        return type;
    }

    public void setType(Byte type) {
        this.type = type;
    }
}
