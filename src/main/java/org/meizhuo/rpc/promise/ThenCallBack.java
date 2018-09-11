package org.meizhuo.rpc.promise;

/**
 * 多级RPC调用之间 无需等待结果的异步RPC操作写在此回调
 * 例如发送邮件服务
 * @param <T> 上一级结果传递作为这一级的参数
 * @param <R> 本次的返回结果 传递给下一级做为参数
 */
public interface ThenCallBack<T,R> extends RxCallBack{

    R done(T arg);

}
