package org.meizhuo.rpc.promise;

public interface Promise {

    Promise then(ThenCallBack thenCallBack);

    Promise success(SucessCallBack sucessCallBack);

    Promise fail(FailCallback failCallback);

}
