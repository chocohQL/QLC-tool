package com.chocoh.ql.core.curve;

import com.chocoh.ql.core.function.TriFunction;
import com.chocoh.ql.core.function.TriPredicate;

import java.util.Map;
import java.util.function.BiConsumer;

/**
 * @author chocoh
 */
@SuppressWarnings("UnusedReturnValue")
public interface ICurveGroup<K, T, V> extends Map<K, ICurve<T, V>> {
    ICurveGroup<K, T, V> process(BiConsumer< K, ICurve<T, V>> biC);

    <U> ICurveGroup<K, T, V> biProcess(ICurveGroup<K, U, V> group, TriFunction< K,  T,  U,  V> biF, BiConsumer< T,  V> biC);

    <U> ICurveGroup<K, T, V> biProcess(ICurveGroup<K, U, V> group, TriPredicate< K,  T,  U> biP, TriFunction< K,  T,  U,  V> biF, BiConsumer< T,  V> biC);
}
