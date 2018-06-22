package org.meizhuo.rpc.promise;

import java.util.ArrayList;
import java.util.List;

public class PromiseImpl implements Promise {

    List<ThenCallBack> thenCallBackList=new ArrayList<>();
    private SucessCallBack sucessCallBack;

    @Override
    public Promise then(ThenCallBack thenCallBack) {
        //不执行具体操作 只返回promise并保存其回调 具体操作在回调完成时执行
        thenCallBackList.add(thenCallBack);
        return this;
    }

    @Override
    public Promise success(SucessCallBack sucessCallBack) {
        this.sucessCallBack=sucessCallBack;
        //不执行具体操作 只返回promise并保存其回调 具体操作在回调完成时执行
        return this;
    }

    public void resolve(){
        sucessCallBack.done();
    }
}
