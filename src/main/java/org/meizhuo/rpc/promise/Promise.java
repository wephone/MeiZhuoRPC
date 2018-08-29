package org.meizhuo.rpc.promise;

public interface Promise {

    Promise then(RxCallBack thenCallBack);

    Promise success(SucessCallBack sucessCallBack);

    Promise fail(FailCallback failCallback);

}
