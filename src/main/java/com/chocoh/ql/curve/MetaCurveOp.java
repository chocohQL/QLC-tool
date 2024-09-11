package com.chocoh.ql.curve;

import java.util.function.*;

/**
 * @author chocoh
 */
public interface MetaCurveOp<T, V> {
    void process(T t, Predicate<T> p, Function<T, V> f, BiConsumer<T, V> biC);

    <U> void biProcess(T t, U u, BiPredicate<T, U> biP, BiFunction<T, U, V> biF, BiConsumer<T, V> biC);

    ICurve<T, V> traversal(Consumer<T> c);

    <U> ICurve<T, V> biTraversal(ICurve<U, V> c, BiConsumer<T, U> biC);
}
