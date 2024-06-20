package com.chocoh.ql.curve;

import com.chocoh.ql.function.TriConsumer;
import com.chocoh.ql.function.TriFunction;
import com.chocoh.ql.function.TriPredicate;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;

/**
 * 分组曲线元操作
 *
 * @author chocoh
 */
public interface MetaGroupOp<K, T, V> {
    void process(ICurve<T, V> c, K k, BiConsumer<K, T> biC);

    void process(ICurve<T, V> c, K key, BiPredicate<K, T> biP, BiFunction<K, T, V> biF, BiConsumer<T, V> biC);

    <U> void biProcess(ICurve<T, V> c1, ICurve<U, V> c2, K k, TriPredicate<K, T, U> triP, TriFunction<K, T, U, V> triF, BiConsumer<T, V> biC);

    ICurveGroup<K, T, V> keySetTraversal(BiConsumer<K, ICurve<T, V>> biC);

    <U> ICurveGroup<K, T, V> keySetTraversal(ICurveGroup<K, U, V> cG, TriConsumer<K, ICurve<T, V>, ICurve<U, V>> triC);
}
