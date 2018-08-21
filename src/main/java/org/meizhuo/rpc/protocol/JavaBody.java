package org.meizhuo.rpc.protocol;

public class JavaBody {

    /**
     * RPC服务名字节长度
     */
    private Integer serviceLength;
    /**
     * RPC服务名
     */
    private String service;
    /**
     * 被调用方法名字节长度
     */
    private Integer methodLength;
    /**
     * 被调用方法名
     */
    private String method;
    /**
     * 目标方法参数个数
     */
    private Integer argsNum;
    /**
     * 参数列表
     */
    private Arg[] args;
    /**
     * 返回结果类名字节长度
     */
    private Integer resultNameLength;
    /**
     * 返回结果类名
     */
    private String resultName;
    /**
     * 返回内容字节长度
     */
    private Integer resultLength;
    /**
     * 返回内容
     */
    private byte[] result;
    /**
     * 参数对象内部类
     */
    class Arg{

        /**
         * 参数名字节长度
         */
        private Integer argNameLength;
        /**
         * 参数名
         */
        private String argName;
        /**
         * 参数内容长度
         */
        private Integer contentLength;
        /**
         * 参数内容
         */
        private byte[] content;

        public String getArgName() {
            return argName;
        }

        public void setArgName(String argName) {
            this.argName = argName;
        }

        public Integer getArgNameLength() {
            return argNameLength;
        }

        public void setArgNameLength(Integer argNameLength) {
            this.argNameLength = argNameLength;
        }

        public Integer getContentLength() {
            return contentLength;
        }

        public void setContentLength(Integer contentLength) {
            this.contentLength = contentLength;
        }

        public byte[] getContent() {
            return content;
        }

        public void setContent(byte[] content) {
            this.content = content;
        }
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public Integer getServiceLength() {
        return serviceLength;
    }

    public void setServiceLength(Integer serviceLength) {
        this.serviceLength = serviceLength;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public Integer getMethodLength() {
        return methodLength;
    }

    public void setMethodLength(Integer methodLength) {
        this.methodLength = methodLength;
    }

    public Integer getArgsNum() {
        return argsNum;
    }

    public void setArgsNum(Integer argsNum) {
        this.argsNum = argsNum;
    }

    public Arg[] getArgs() {
        return args;
    }

    public void setArgs(Arg[] args) {
        this.args = args;
    }

    public String getResultName() {
        return resultName;
    }

    public void setResultName(String resultName) {
        this.resultName = resultName;
    }

    public Integer getResultNameLength() {
        return resultNameLength;
    }

    public void setResultNameLength(Integer resultNameLength) {
        this.resultNameLength = resultNameLength;
    }

    public byte[] getResult() {
        return result;
    }

    public void setResult(byte[] result) {
        this.result = result;
    }

    public Integer getResultLength() {
        return resultLength;
    }

    public void setResultLength(Integer resultLength) {
        this.resultLength = resultLength;
    }
}
