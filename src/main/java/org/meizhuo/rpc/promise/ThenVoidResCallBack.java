package org.meizhuo.rpc.promise;

public interface ThenVoidResCallBack<T> extends RxCallBack{

    void done(T arg);

}
