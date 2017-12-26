package org.meizhuo.rpc.server;

import java.util.Map;

/**
 * Created by wephone on 17-12-26.
 */
public class ServerConfig {

    private Map<String,Object> serverImplMap;

    public Map<String, Object> getServerImplMap() {
        return serverImplMap;
    }

    public void setServerImplMap(Map<String, Object> serverImplMap) {
        this.serverImplMap = serverImplMap;
    }
}
