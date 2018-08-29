package org.meizhuo.rpc.promise;

public interface ThenVoidCallBack<T> extends RxCallBack{

    T done();

}
