package com.chocoh.ql.core.function;

/**
 * @author chocoh
 */
@FunctionalInterface
public interface TriConsumer<T, U, V> {
    void accept(T t, U u, V v);
}
