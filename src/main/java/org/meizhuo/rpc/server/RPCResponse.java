package org.meizhuo.rpc.server;

/**
 * Created by wephone on 17-12-27.
 */
public class RPCResponse {

    //每个请求对应的唯一ID
    private String requestID;
    private Object result;
    private Long receivedTime;
    private String serviceId;
    //本次响应的traceId
    private String traceId;
    //本次响应的spanId
    private String spanId;

    public String getRequestID() {
        return requestID;
    }

    public void setRequestID(String requestID) {
        this.requestID = requestID;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }

    public Long getReceivedTime() {
        return receivedTime;
    }

    public void setReceivedTime(Long receivedTime) {
        this.receivedTime = receivedTime;
    }

    public String getTraceId() {
        return traceId;
    }

    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }

    public String getSpanId() {
        return spanId;
    }

    public void setSpanId(String spanId) {
        this.spanId = spanId;
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }
}
