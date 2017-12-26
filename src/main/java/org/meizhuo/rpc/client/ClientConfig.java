package org.meizhuo.rpc.client;

/**
 * Created by wephone on 17-12-26.
 */
public class ClientConfig {

    private String host;
    private int port;
    //调用超时时间
    private long overtime;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public long getOvertime() {
        return overtime;
    }

    public void setOvertime(long overtime) {
        this.overtime = overtime;
    }
}
