package com.chocoh.ql.core.function;

/**
 * @author chocoh
 */
@FunctionalInterface
public interface TriPredicate<T, U, V> {
    boolean test(T t, U u, V v);
}
