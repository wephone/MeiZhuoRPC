package org.meizhuo.rpc.protocol;

import org.meizhuo.rpc.client.RPCRequest;
import org.meizhuo.rpc.server.RPCResponse;

public class MZProtocol implements RPCProtocol{

    private Header header;
    private Body body;

    @Override
    public void buildRequestProtocol(RPCRequest rpcRequest) {

    }

    @Override
    public void buildResponseProtocol(RPCResponse rpcResponse) {

    }

    @Override
    public RPCRequest buildRequestByProtocol() {
        return null;
    }
}
