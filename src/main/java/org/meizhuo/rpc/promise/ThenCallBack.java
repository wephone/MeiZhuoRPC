package org.meizhuo.rpc.promise;

public interface ThenCallBack<T> {

    Promise done(T data);

}
