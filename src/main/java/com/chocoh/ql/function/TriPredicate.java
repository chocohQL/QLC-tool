package com.chocoh.ql.function;

/**
 * @author chocoh
 */
@FunctionalInterface
public interface TriPredicate<T, U, V> {
    boolean test(T t, U u, V v);
}
