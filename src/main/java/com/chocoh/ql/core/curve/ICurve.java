package com.chocoh.ql.core.curve;

import java.util.List;
import java.util.function.*;

/**
 * @author chocoh
 */
@SuppressWarnings("UnusedReturnValue")
public interface ICurve<T, V> {
    Curve<T, V> process(Consumer<T> c);

    Curve<T, V> process(Function<T, V> f, BiConsumer<T, V> biC);

    Curve<T, V> process(Predicate<T> p, Function<T, V> f, BiConsumer<T, V> biC);

    <U> Curve<T, V> biProcess(ICurve<U, V> curve, BiFunction<T, U, V> biF, BiConsumer<T, V> biC);

    <U> Curve<T, V> biProcess(ICurve<U, V> curve, BiPredicate<T, U> biP, BiFunction<T, U, V> biF, BiConsumer<T, V> biC);

    <U> Curve<T, V> multiProcess(List<ICurve<U, V>> curves, BiFunction<T, U, V> biF, BiConsumer<T, V> biC);

    <U> Curve<T, V> multiProcess(List<ICurve<U, V>> curves, BiPredicate<T, U> biP, BiFunction<T, U, V> biF, BiConsumer<T, V> biC);

    Curve<T, V> mergeCurve(ICurve<T, V> curve);

    Curve<T, V> mergeCurve(List<ICurve<T, V>> curves);

    List<T> getData();

    void setData(List<T> data);

    Consumer<V> getPostProcessor();

    void setPostProcessor(Consumer<V> postProcessor);
}
