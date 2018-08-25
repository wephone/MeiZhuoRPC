package org.meizhuo.rpc.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;

import java.util.List;

public class JavaRPCEncoder extends MessageToMessageEncoder<MZJavaProtocol> {

    /**
     * 将协议参数写入字节区
     */
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, MZJavaProtocol protocol, List<Object> list) throws Exception {
        ByteBuf byteBuf=Unpooled.buffer();
        byteBuf.writeInt(protocol.getHeader().getLength());
        byteBuf.writeInt(protocol.getHeader().getTraceIdLength());
        byteBuf.writeBytes(protocol.getHeader().getTraceId().getBytes());
        byteBuf.writeInt(protocol.getHeader().getSpanIdLength());
        byteBuf.writeBytes(protocol.getHeader().getSpanId().getBytes());
        byteBuf.writeInt(protocol.getHeader().getRequestIdLength());
        byteBuf.writeBytes(protocol.getHeader().getRequestId().getBytes());
        byteBuf.writeByte(protocol.getHeader().getType());
        if (protocol.getHeader().getType().equals(Header.T_REQ)) {
            byteBuf.writeInt(protocol.getJavaBody().getServiceLength());
            byteBuf.writeBytes(protocol.getJavaBody().getService().getBytes());
            byteBuf.writeInt(protocol.getJavaBody().getMethodLength());
            byteBuf.writeBytes(protocol.getJavaBody().getMethod().getBytes());
            byteBuf.writeInt(protocol.getJavaBody().getArgsNum());
            for (int i = 0; i < protocol.getJavaBody().getArgsNum(); i++) {
                byteBuf.writeInt(protocol.getJavaBody().getArgs()[i].getArgNameLength());
                byteBuf.writeBytes(protocol.getJavaBody().getArgs()[i].getArgName().getBytes());
                byteBuf.writeInt(protocol.getJavaBody().getArgs()[i].getContentLength());
                byteBuf.writeBytes(protocol.getJavaBody().getArgs()[i].getContent());
            }
        }else {
            //不是发出请求的话 写入返回结果的协议信息
            byteBuf.writeInt(protocol.getJavaBody().getResultNameLength());
            byteBuf.writeBytes(protocol.getJavaBody().getResultName().getBytes());
            byteBuf.writeInt(protocol.getJavaBody().getResultLength());
            byteBuf.writeBytes(protocol.getJavaBody().getResult());
        }
    }
}
