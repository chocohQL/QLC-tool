package com.chocoh.ql.curve;

import java.util.*;
import java.util.function.*;

/**
 * 曲线
 *
 * @author chocoh
 */
public class Curve<T, V> extends AbstractCurve<T, V> {
    @Override
    public ICurve<T, V> process(Consumer<T> c) {
        return traversal(c);
    }

    @Override
    public ICurve<T, V> process(Function<T, V> f, BiConsumer<T, V> biC) {
        return traversal(t -> process(t, null, f, biC));
    }

    @Override
    public ICurve<T, V> process(Predicate<T> p, Function<T, V> f, BiConsumer<T, V> biC) {
        return traversal(t -> process(t, p, f, biC));
    }

    @Override
    public <U> ICurve<T, V> biProcess(ICurve<U, V> curve, BiFunction<T, U, V> biF, BiConsumer<T, V> biC) {
        return biTraversal(curve, (t, u) -> biProcess(t, u, null, biF, biC));
    }

    @Override
    public <U> ICurve<T, V> biProcess(ICurve<U, V> curve, BiPredicate<T, U> biP, BiFunction<T, U, V> biF, BiConsumer<T, V> biC) {
        return biTraversal(curve, (t, u) -> biProcess(t, u, biP, biF, biC));
    }

    @Override
    public <U> ICurve<T, V> multiProcess(List<ICurve<U, V>> curves, BiFunction<T, U, V> biF, BiConsumer<T, V> biC) {
        return multiTraversal(curves, c -> biProcess(c, null, biF, biC));
    }

    @Override
    public <U> ICurve<T, V> multiProcess(List<ICurve<U, V>> curves, BiPredicate<T, U> biP, BiFunction<T, U, V> biF, BiConsumer<T, V> biC) {
        return multiTraversal(curves, c -> biProcess(c, biP, biF, biC));
    }

    public static <T, V> ICurve<T, V> create(List<T> l) {
        return new Curve<>(l);
    }

    public Curve(List<T> l) {
        super(l);
    }

    public Curve() {
    }
}
