package org.meizhuo.rpc.protocol;

import java.io.*;

/**
 * 对象 字节数组的相互转换 用于Java协议的复杂对象序列化反序列化
 */
public class ObjToBytesUtils {


    public static byte[] objectToBytes(Object obj){
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ObjectOutputStream sOut = null;
        try {
            sOut = new ObjectOutputStream(out);
            sOut.writeObject(obj);
            sOut.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        byte[] bytes = out.toByteArray();

        return bytes;
    }

    public static Object bytesToObject(byte[] bytes) {
        ByteArrayInputStream in = new ByteArrayInputStream(bytes);
        ObjectInputStream sIn = null;
        Object o=null;
        try {
            sIn = new ObjectInputStream(in);
            o=sIn.readObject();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return o;
    }


}
