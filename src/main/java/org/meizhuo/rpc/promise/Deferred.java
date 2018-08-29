package org.meizhuo.rpc.promise;

import org.meizhuo.rpc.threadLocal.NeedReturnThreadLocal;
import org.meizhuo.rpc.threadLocal.PromiseThreadLocal;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class Deferred implements Promise {

    Queue thenCallBackList=new LinkedList<>();
    private SucessCallBack sucessCallBack;
    private FailCallback failCallback;
    //TODO 每次首个开启异步RPC的操作保持一个调用链traceId 不存在则创建并保存到threadLocal
    private String traceId;
    private String parentSpanId;
//    //是否可以直接循环执行所有回调 当回调中有其他异步RPC时不可继续循环
//    private AtomicInteger loop=new AtomicInteger();
//
//    public void increaseLoop() {
//        loop.incrementAndGet();
//    }
//
//    public void reduceLoop() {
//        loop.decrementAndGet();
//    }


    public String getTraceId() {
        return traceId;
    }

    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }

    public String getParentSpanId() {
        return parentSpanId;
    }

    public void setParentSpanId(String parentSpanId) {
        this.parentSpanId = parentSpanId;
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
        //判断此时loop是否为0 是的话继续循环 不是的话说明还有异步调用未返回退出循环
        boolean loop=true;
        while (loop) {
            //每次取出队列的第一个执行真正的操作
            Object callBack = thenCallBackList.poll();
            if (callBack != null) {
                // 在此处设置promise到threadLocal 保持全链路promise唯一
                PromiseThreadLocal.setThreadPromise(this);
                if (callBack instanceof ThenCallBack){
                    //设置then方法的异步RPC不处理返回结果
                    NeedReturnThreadLocal.noNeedReturn();
                    //上级回调结果作为此级参数
                    argForThen=((ThenCallBack)callBack).done(argForThen);
                    NeedReturnThreadLocal.removeNeedReturn();
                }else if (callBack instanceof ThenVoidArgCallBack){
                    //设置then方法的异步RPC不处理返回结果
                    NeedReturnThreadLocal.noNeedReturn();
                    //此级回调无需参数
                    argForThen=((ThenVoidArgCallBack)callBack).done();
                    NeedReturnThreadLocal.removeNeedReturn();
                }else if (callBack instanceof ThenVoidResCallBack){
                    //设置then方法的异步RPC不处理返回结果
                    NeedReturnThreadLocal.noNeedReturn();
                    ((ThenVoidResCallBack)callBack).done(argForThen);
                    NeedReturnThreadLocal.removeNeedReturn();
                    argForThen=null;
                }else if (callBack instanceof ThenVoidCallBack){
                    //设置then方法的异步RPC不处理返回结果
                    NeedReturnThreadLocal.noNeedReturn();
                    ((ThenVoidCallBack)callBack).done();
                    NeedReturnThreadLocal.removeNeedReturn();
                    argForThen=null;
                }else if (callBack instanceof NextCallBack){
                    //下一级RPC回调 返回参数不用设置 因为此时会结束循环
                    ((NextCallBack)callBack).nextRPC(argForThen);
                    //next调用不可继续后续事件 需要等待RPC返回
                    loop=false;
                }else if (callBack instanceof NextVoidArgCallBack){
                    //下一级无参RPC回调 返回参数不用设置 因为此时会结束循环
                    ((NextVoidArgCallBack)callBack).nextRPC();
                    //next调用不可继续后续事件 需要等待RPC返回
                    loop=false;
                }
                // 释放threadLocal的Promise
                PromiseThreadLocal.removeThreadPromise();
            } else {
                if (sucessCallBack!=null) {
                    sucessCallBack.done(argForThen);
                }
                break;
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
