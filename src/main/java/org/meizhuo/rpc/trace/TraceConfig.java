package org.meizhuo.rpc.trace;

public class TraceConfig {

    private String zipkinUrl;
    private Integer HTTPMaxIdle=2;
    private Integer HTTPMaxTotal=4;
    //配置vm参数使用定制ThreadLocal才可以触发链路追踪
    private Boolean enableTrace=false;
    private Integer appId=1;

    public String getZipkinUrl() {
        return zipkinUrl;
    }

    public void setZipkinUrl(String zipkinUrl) {
        this.zipkinUrl = zipkinUrl;
    }

    public Integer getHTTPMaxIdle() {
        return HTTPMaxIdle;
    }

    public void setHTTPMaxIdle(Integer HTTPMaxIdle) {
        this.HTTPMaxIdle = HTTPMaxIdle;
    }

    public Integer getHTTPMaxTotal() {
        return HTTPMaxTotal;
    }

    public void setHTTPMaxTotal(Integer HTTPMaxTotal) {
        this.HTTPMaxTotal = HTTPMaxTotal;
    }

    public Boolean getEnableTrace() {
        return enableTrace;
    }

    public void setEnableTrace(Boolean enableTrace) {
        this.enableTrace = enableTrace;
    }

    public Integer getAppId() {
        return appId;
    }

    public void setAppId(Integer appId) {
        this.appId = appId;
    }
}
