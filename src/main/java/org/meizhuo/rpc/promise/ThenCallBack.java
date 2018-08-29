package org.meizhuo.rpc.promise;

/**
 * then类回调用于多级RPC操作之间的非RPC操作
 * 例如2次异步RPC之间需要一次查询数据库操作并将数据库结果传给第二次RPC 就需要then回调
 * @param <T> 上一级结果传递作为这一级的参数
 * @param <R> 本次next的返回结果 传递给下一级做为参数
 */
public interface ThenCallBack<T,R> extends RxCallBack{

    R done(T arg);

}
