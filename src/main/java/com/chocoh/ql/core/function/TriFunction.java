package com.chocoh.ql.core.function;

/**
 * @author chocoh
 */
@FunctionalInterface
public interface TriFunction<T, U, V, R> {
    R apply(T t, U u, V v);
}
