package org.meizhuo.rpc.promise;

/**
 * Next类回调用于连续的下一级异步RPC调用 固定返回promise
 * @param <T> 上一级返回结果 作为本次RPC的入参
 */
public interface NextCallBack<T> extends RxCallBack{

    Promise nextRPC(T arg);

}
