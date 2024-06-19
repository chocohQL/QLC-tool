package com.chocoh.ql.curve;

import com.chocoh.ql.function.TriFunction;
import com.chocoh.ql.function.TriPredicate;

import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;

/**
 * @author chocoh
 */
@SuppressWarnings("UnusedReturnValue")
public interface ICurveGroup<K, T, V> extends Map<K, ICurve<T, V>> {
    ICurveGroup<K, T, V> process(BiConsumer<K, T> biC);

    ICurveGroup<K, T, V> process(BiFunction<K, T, V> biF, BiConsumer<T, V> biC);

    ICurveGroup<K, T, V> process(BiFunction<K, T, V> biF, BiPredicate<K, T> biP, BiConsumer<T, V> biC);

    <U> ICurveGroup<K, T, V> biProcess(ICurveGroup<K, U, V> cG, TriFunction<K, T, U, V> triF, BiConsumer<T, V> biC);

    <U> ICurveGroup<K, T, V> biProcess(ICurveGroup<K, U, V> cG, TriPredicate<K, T, U> triP, TriFunction<K, T, U, V> triF, BiConsumer<T, V> biC);
}
