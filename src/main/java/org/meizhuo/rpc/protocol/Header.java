package org.meizhuo.rpc.protocol;

public class Header {

    /**
     * 预留用于链路追踪
     */
    private Integer traceIdLength;
    private String traceId;
    /**
     * 预留用于链路追踪
     */
    private Integer spanIdLength;
    private String spanId;
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

    public Integer getTraceIdLength() {
        return traceIdLength;
    }

    public void setTraceIdLength(Integer traceIdLength) {
        this.traceIdLength = traceIdLength;
    }

    public String getTraceId() {
        return traceId;
    }

    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }

    public Integer getSpanIdLength() {
        return spanIdLength;
    }

    public void setSpanIdLength(Integer spanIdLength) {
        this.spanIdLength = spanIdLength;
    }

    public String getSpanId() {
        return spanId;
    }

    public void setSpanId(String spanId) {
        this.spanId = spanId;
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
