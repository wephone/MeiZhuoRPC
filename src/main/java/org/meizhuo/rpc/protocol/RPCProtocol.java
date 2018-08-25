package org.meizhuo.rpc.protocol;

import org.meizhuo.rpc.client.RPCRequest;
import org.meizhuo.rpc.server.RPCResponse;

public interface RPCProtocol {

    void buildRequestProtocol(RPCRequest rpcRequest);

    void buildResponseProtocol(RPCResponse rpcResponse);

    RPCRequest buildRequestByProtocol();

}
