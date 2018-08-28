package org.meizhuo.rpc.trace;

public class TraceConfig {

    private String zipkinUrl;
    private Integer HTTPMaxIdle=2;
    private Integer HTTPMaxTotal=4;

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
}
