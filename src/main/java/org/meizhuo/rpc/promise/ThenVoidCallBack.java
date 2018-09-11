package org.meizhuo.rpc.promise;

@Deprecated
public interface ThenVoidCallBack<T> extends RxCallBack{

    T done();

}
