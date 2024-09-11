package com.chocoh.ql.group;

import com.chocoh.ql.curve.ICurve;
import com.chocoh.ql.function.TriConsumer;
import com.chocoh.ql.function.TriFunction;
import com.chocoh.ql.function.TriPredicate;

import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;

/**
 * @author chocoh
 */
public interface ICurveGroup<K, T, V> extends Map<K, ICurve<T, V>> {
    ICurveGroup<K, T, V> process(BiConsumer<K, T> biC);

    ICurveGroup<K, T, V> process(BiFunction<K, T, V> biF, BiConsumer<T, V> biC);

    ICurveGroup<K, T, V> process(BiPredicate<K, T> biP, BiFunction<K, T, V> biF, BiConsumer<T, V> biC);

    <U> ICurveGroup<K, T, V> biProcess(ICurveGroup<K, U, V> cG, TriFunction<K, T, U, V> triF, BiConsumer<T, V> biC);

    <U> ICurveGroup<K, T, V> biProcess(ICurveGroup<K, U, V> cG, TriPredicate<K, T, U> triP, TriFunction<K, T, U, V> triF, BiConsumer<T, V> biC);

    ICurveGroup<K, T, V> forCurve(BiConsumer<K, ICurve<T, V>> biC);

    <U> ICurveGroup<K, T, V> forCurve(ICurveGroup<K, U, V> cG, TriConsumer<K, ICurve<T, V>, ICurve<U, V>> biC);
}
