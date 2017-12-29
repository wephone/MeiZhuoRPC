package org.meizhuo.rpc.client.protobuf;

import com.google.protobuf.InvalidProtocolBufferException;
import org.junit.Test;

/**
 * Created by wephone on 17-12-29.
 */
public class ProtoBufTest {

    private byte[] endcode(RPCRequestProto.RPCRequest rpcRequest){
        return rpcRequest.toByteArray();
    }

    private RPCRequestProto.RPCRequest decode(byte[] body) throws InvalidProtocolBufferException {
        return RPCRequestProto.RPCRequest.parseFrom(body);
    }

    private RPCRequestProto.RPCRequest createRPCRequest(){
        RPCRequestProto.RPCRequest.Builder builder= RPCRequestProto.RPCRequest.newBuilder();
        builder.setRequestID("23333");
        builder.setMethodName("test");
        builder.setClassName("class");
        return builder.build();
    }

    @Test
    public void testProtoBuf() throws InvalidProtocolBufferException {
        RPCRequestProto.RPCRequest rpcRequest=createRPCRequest();
        System.out.println("解码前:"+rpcRequest.toString());
        RPCRequestProto.RPCRequest rpcRequest2 =decode(endcode(rpcRequest));
        System.out.println("解码后重新编码:"+rpcRequest.toString());
        System.out.println("是否相等:"+rpcRequest2.equals(rpcRequest));
    }

}
