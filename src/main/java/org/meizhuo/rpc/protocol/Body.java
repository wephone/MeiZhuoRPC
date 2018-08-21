package org.meizhuo.rpc.protocol;

public class Body {

    /**
     * RPC服务名
     */
    private String service;
    /**
     * RPC服务名长度
     */
    private Integer serviceLength;
    /**
     * 被调用方法名
     */
    private String method;
    /**
     * 被调用方法名长度
     */
    private Integer methodLength;
    /**
     * 目标方法参数个数
     */
    private Integer argsNum;
    /**
     * 参数列表
     */
    private Arg[] args;
    /**
     * 返回结果的tag
     */
    private Byte resultTag;
    /**
     * 返回内容
     */
    private Byte[] result;
    /**
     * 返回内容长度
     */
    private Integer resultLength;

    /**
     * 参数对象内部类
     */
    class Arg{
        /**
         * 参数类型tag
         */
        private Integer argTag;
        /**
         * 参数内容
         */
        private Byte[] content;

        public Integer getArgTag() {
            return argTag;
        }

        public void setArgTag(Integer argTag) {
            this.argTag = argTag;
        }

        public Byte[] getContent() {
            return content;
        }

        public void setContent(Byte[] content) {
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

    public Byte getResultTag() {
        return resultTag;
    }

    public void setResultTag(Byte resultTag) {
        this.resultTag = resultTag;
    }

    public Byte[] getResult() {
        return result;
    }

    public void setResult(Byte[] result) {
        this.result = result;
    }

    public Integer getResultLength() {
        return resultLength;
    }

    public void setResultLength(Integer resultLength) {
        this.resultLength = resultLength;
    }
}
