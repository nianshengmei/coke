package pers.warren.ioc.pool;

import java.util.concurrent.Future;

public interface CokeFuture<T,R> extends Future<T> {

    /**
     * 异步获取属性
     */
    CokeFuture<R,?> getAttributeFuture();





}
