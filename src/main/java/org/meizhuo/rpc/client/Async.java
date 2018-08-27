package org.meizhuo.rpc.client;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 运行时可读取注解
 * 用于注解异步RPC类
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Async {
}
