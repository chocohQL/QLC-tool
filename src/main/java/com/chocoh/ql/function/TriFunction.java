package com.chocoh.ql.function;

/**
 * @author chocoh
 */
@FunctionalInterface
public interface TriFunction<T, U, V, R> {
    R apply(T t, U u, V v);
}
