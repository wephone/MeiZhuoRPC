package org.meizhuo.rpc.protocol;

/**
 * Created by wephone on 18-08-21.
 */
public class MZJavaProtocol {

    private Header header;
    private JavaBody javaBody;

    public Header getHeader() {
        return header;
    }

    public void setHeader(Header header) {
        this.header = header;
    }

    public JavaBody getJavaBody() {
        return javaBody;
    }

    public void setJavaBody(JavaBody javaBody) {
        this.javaBody = javaBody;
    }
}
