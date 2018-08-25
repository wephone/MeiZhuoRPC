package org.meizhuo.rpc.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LineBasedFrameDecoder;

public class JavaRPCDecoder extends LineBasedFrameDecoder {


    public JavaRPCDecoder(int maxLength) {
        super(maxLength);
    }

    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        //调用父类decode  得到的就是一个按换行符划分的完整包 无需再进行粘包拆包
        ByteBuf byteBuf= (ByteBuf) super.decode(ctx, in);
        if(byteBuf==null){
            return null;
        }
        MZJavaProtocol protocol=new MZJavaProtocol();
        Header header=new Header();
        JavaBody body=new JavaBody();
        //取出前4字节 以此类推
        Integer traceLength=byteBuf.readInt();
        header.setTraceIdLength(traceLength);
        byte[] traceBytes=new byte[traceLength];
        byteBuf.readBytes(traceBytes);
        header.setTraceId(new String(traceBytes));
        Integer spanLength=byteBuf.readInt();
        header.setSpanIdLength(spanLength);
        byte[] spanBytes=new byte[traceLength];
        byteBuf.readBytes(spanBytes);
        header.setSpanId(new String(spanBytes));
        header.setRequestId(byteBuf.readLong());
        header.setType(byteBuf.readByte());
        //先读出对应字符串的长度 开辟一个改长度的字节数组 再读取这么多长度的内容到这个字节数组里 最后转换为需要的类型
        int serviceLength=byteBuf.readInt();
        body.setServiceLength(serviceLength);
        byte[] serviceBytes=new byte[serviceLength];
        byteBuf.readBytes(serviceBytes);
        body.setService(new String(serviceBytes));
        int methodLength=byteBuf.readInt();
        byte[] methodBytes=new byte[methodLength];
        byteBuf.readBytes(methodBytes);
        body.setMethod(new String(methodBytes));
        int argNum=byteBuf.readInt();
        body.setArgsNum(argNum);
        JavaBody.Arg[] args=new JavaBody.Arg[argNum];
        for (int i = 0; i <argNum ; i++) {
            int argNameLength=byteBuf.readInt();
            args[i].setArgNameLength(argNameLength);
            byte[] argNameBytes=new byte[argNameLength];
            byteBuf.readBytes(argNameBytes);
            args[i].setArgName(new String(argNameBytes));
            int contentLength=byteBuf.readInt();
            args[i].setContentLength(contentLength);
            byte[] contentBytes=new byte[contentLength];
            byteBuf.readBytes(contentBytes);
            args[i].setContent(contentBytes);
        }
        if (!header.getType().equals(Header.T_REQ)){
            int resultNameLength=byteBuf.readInt();
            body.setResultNameLength(resultNameLength);
            byte[] resultNameBytes=new byte[resultNameLength];
            byteBuf.readBytes(resultNameBytes);
            body.setResultName(new String(resultNameBytes));
            int resultLength=byteBuf.readInt();
            byte[] result=new byte[resultLength];
            byteBuf.readBytes(result);
            body.setResult(result);
        }
        protocol.setHeader(header);
        protocol.setJavaBody(body);
        return protocol;
    }
}
