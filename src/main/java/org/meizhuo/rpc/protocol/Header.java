package org.meizhuo.rpc.protocol;

public class Header {

    /**
     * 整个包的长度 用于区分TCP半包粘包
     */
    private Integer length;
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
    private Integer requestIdLength;
    private String requestId;
    /**
     * 调用类型 1:RPC请求 2:RPC响应 2:异常RPC响应
     */
    private Byte type;

    public final static Byte T_REQ=1;
    public final static Byte T_RESP=2;
    public final static Byte T_EX_RESP=3;

    public Integer getLength() {
        return length;
    }

    public void setLength(Integer length) {
        this.length = length;
    }

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

    public Integer getRequestIdLength() {
        return requestIdLength;
    }

    public void setRequestIdLength(Integer requestIdLength) {
        this.requestIdLength = requestIdLength;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public Byte getType() {
        return type;
    }

    public void setType(Byte type) {
        this.type = type;
    }
}
