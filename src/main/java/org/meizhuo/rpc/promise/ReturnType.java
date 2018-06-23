package org.meizhuo.rpc.promise;

public @interface ReturnType {

    Class value() default Object.class;

}
