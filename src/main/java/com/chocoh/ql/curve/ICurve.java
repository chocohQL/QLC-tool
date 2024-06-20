package com.chocoh.ql.curve;

import java.util.List;
import java.util.function.*;

/**
 * 曲线
 *
 * @author chocoh
 */
@SuppressWarnings("UnusedReturnValue")
public interface ICurve<T, V> extends List<T> {
    ICurve<T, V> process(Consumer<T> c);

    ICurve<T, V> process(Function<T, V> f, BiConsumer<T, V> biC);

    ICurve<T, V> process(Predicate<T> p, Function<T, V> f, BiConsumer<T, V> biC);

    <U> ICurve<T, V> biProcess(ICurve<U, V> curve, BiFunction<T, U, V> biF, BiConsumer<T, V> biC);

    <U> ICurve<T, V> biProcess(ICurve<U, V> curve, BiPredicate<T, U> biP, BiFunction<T, U, V> biF, BiConsumer<T, V> biC);

    <U> ICurve<T, V> multiProcess(List<ICurve<U, V>> cs, BiFunction<T, U, V> biF, BiConsumer<T, V> biC);

    <U> ICurve<T, V> multiProcess(List<ICurve<U, V>> cs, BiPredicate<T, U> biP, BiFunction<T, U, V> biF, BiConsumer<T, V> biC);
}
