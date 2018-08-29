package org.meizhuo.rpc.promise;

import org.meizhuo.rpc.threadLocal.PromiseThreadLocal;

import java.util.LinkedList;
import java.util.Queue;

public class Deferred implements Promise {

    Queue thenCallBackList=new LinkedList<>();
    private SucessCallBack sucessCallBack;
    private FailCallback failCallback;
    //TODO 每次首个开启异步RPC的操作保持一个调用链traceId 不存在则创建并保存到threadLocal
    private String traceId;
    private String parentSpanId;
    //是否可以直接循环执行所有回调 当回调中有其他异步RPC时不可继续循环
    private Boolean loop;

    public void setLoop(Boolean loop) {
        this.loop = loop;
    }

    @Override
    public Promise then(RxCallBack thenCallBack) {
        //不执行具体操作 只返回promise并保存其回调 具体操作在回调完成时执行
        thenCallBackList.offer(thenCallBack);
        return this;
    }

    @Override
    public Promise success(SucessCallBack sucessCallBack) {
        this.sucessCallBack=sucessCallBack;
        //不执行具体操作 只返回promise并保存其回调 具体操作在回调完成时执行
        return this;
    }

    @Override
    public Promise fail(FailCallback failCallback) {
        this.failCallback=failCallback;
        return this;
    }

    public void resolve(Object result){
        //上一层的RPC调用作为下一层的入参 RPC调用就算是无返回值也会得到null结果
        Object argForThen=result;
        //TODO 判断此时loop是否为true 是的话继续循环 直到出现false为止
        while (loop) {
            //每次取出队列的第一个执行真正的操作
            Object callBack = thenCallBackList.poll();
            if (callBack != null) {
                // 在此处设置promise到threadLocal 保持全链路promise唯一
                PromiseThreadLocal.setThreadPromise(this);
                if (callBack instanceof ThenCallBack){
                    //上级回调结果作为此级参数
                    argForThen=((ThenCallBack)callBack).done(argForThen);
                }else if (callBack instanceof ThenVoidArgCallBack){
                    //此级回调无需参数
                    argForThen=((ThenVoidArgCallBack)callBack).done();
                }else if (callBack instanceof ThenVoidResCallBack){
                    ((ThenVoidResCallBack)callBack).done(argForThen);
                    argForThen=null;
                }else if (callBack instanceof ThenVoidCallBack){
                    ((ThenVoidCallBack)callBack).done();
                    argForThen=null;
                }else if (callBack instanceof NextCallBack){
                    //下一级RPC回调 返回参数不用设置 因为此时会结束循环
                    ((NextCallBack)callBack).nextRPC(argForThen);
                }else if (callBack instanceof NextVoidArgCallBack){
                    //下一级无参RPC回调 返回参数不用设置 因为此时会结束循环
                    ((NextVoidArgCallBack)callBack).nextRPC();
                }
                // 释放threadLocal的Promise
                PromiseThreadLocal.removeThreadPromise();
            } else {
                if (sucessCallBack!=null) {
                    sucessCallBack.done(argForThen);
                }
                //没有中间级后停止
                loop=false;
            }
        }
    }

    public void reject(Exception e){
        this.failCallback.done(e);
    }

    public Promise promise(){
        return this;
    }
}
