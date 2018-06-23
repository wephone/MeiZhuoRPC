package org.meizhuo.rpc.promise;

import java.util.LinkedList;
import java.util.Queue;

public class Deferred implements Promise {

    Queue<ThenCallBack> thenCallBackList=new LinkedList<>();
    private SucessCallBack sucessCallBack;
    private FailCallback failCallback;

    @Override
    public Promise then(ThenCallBack thenCallBack) {
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
        //每次取出队列的第一个执行真正的操作
        ThenCallBack thenCallBack=thenCallBackList.poll();
        if (thenCallBack!=null) {
            thenCallBack.done(result);
        }else {
            this.sucessCallBack.done(result);
        }
    }

    public void reject(Exception e){
        this.failCallback.done(e);
    }

    public Promise promise(){
        return this;
    }
}
