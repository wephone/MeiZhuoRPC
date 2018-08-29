package org.meizhuo.rpc.trace;

public class SpanStruct {

    private String traceId;
    private String parentId;
    private String id;
    private String kind;
    private String name;
    private Long timestamp;
    private Long duration;
    private LocalEndpoint localEndpoint;
    private RemoteEndpoint remoteEndpoint;

    public final static String SERVER_KIND="SERVER";
    public final static String CLIENT_KIND="CLIENT";

    class LocalEndpoint{
        private String serviceName;
        private String ipv4;

        public String getServiceName() {
            return serviceName;
        }

        public void setServiceName(String serviceName) {
            this.serviceName = serviceName;
        }

        public String getIpv4() {
            return ipv4;
        }

        public void setIpv4(String ipv4) {
            this.ipv4 = ipv4;
        }
    }

    class RemoteEndpoint{
        private String serviceName;
        private String ipv4;
        private Integer port;

        public String getServiceName() {
            return serviceName;
        }

        public void setServiceName(String serviceName) {
            this.serviceName = serviceName;
        }

        public String getIpv4() {
            return ipv4;
        }

        public void setIpv4(String ipv4) {
            this.ipv4 = ipv4;
        }

        public Integer getPort() {
            return port;
        }

        public void setPort(Integer port) {
            this.port = port;
        }
    }

    public String getTraceId() {
        return traceId;
    }

    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public Long getDuration() {
        return duration;
    }

    public void setDuration(Long duration) {
        this.duration = duration;
    }

    public LocalEndpoint getLocalEndpoint() {
        return localEndpoint;
    }

    public void setLocalEndpoint(LocalEndpoint localEndpoint) {
        this.localEndpoint = localEndpoint;
    }

    public RemoteEndpoint getRemoteEndpoint() {
        return remoteEndpoint;
    }

    public void setRemoteEndpoint(RemoteEndpoint remoteEndpoint) {
        this.remoteEndpoint = remoteEndpoint;
    }

    public static String getServerKind() {
        return SERVER_KIND;
    }

    public static String getClientKind() {
        return CLIENT_KIND;
    }
}
