package com.chocoh.ql.meta;

import com.chocoh.ql.curve.ICurve;

import java.util.List;
import java.util.function.*;

/**
 * 曲线元操作
 *
 * @author chocoh
 */
public interface MetaCurveOp<T, V> {
    void process(T t, Predicate<T> p, Function<T, V> f, BiConsumer<T, V> biC);

    <U> void biProcess(T t, U u, BiPredicate<T, U> biP, BiFunction<T, U, V> biF, BiConsumer<T, V> biC);

    ICurve<T, V> traversal(Consumer<T> c);

    <U> ICurve<T, V> biTraversal(ICurve<U, V> c, BiConsumer<T, U> biC);

    <U> ICurve<T, V> multiTraversal(List<ICurve<U, V>> cs, Consumer<ICurve<U, V>> c);
}
