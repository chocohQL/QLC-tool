package com.chocoh.ql.group;

import com.chocoh.ql.curve.Curve;
import com.chocoh.ql.curve.ICurve;
import com.chocoh.ql.function.TriFunction;
import com.chocoh.ql.function.TriPredicate;
import com.chocoh.ql.function.TriConsumer;

import java.util.List;
import java.util.Map;
import java.util.function.*;
import java.util.stream.Collectors;

/**
 * @author chocoh
 */
public class CurveGroup<K, T, V> extends AbstractCurveGroup<K, T, V> {
    @Override
    public ICurveGroup<K, T, V> process(BiConsumer<K, T> biC) {
        return keySetTraversal((k, c) -> process(c, k, biC));
    }

    @Override
    public ICurveGroup<K, T, V> process(BiFunction<K, T, V> biF, BiConsumer<T, V> biC) {
        return keySetTraversal((k, c) -> process(c, k, null, biF, biC));
    }

    @Override
    public ICurveGroup<K, T, V> process(BiPredicate<K, T> biP, BiFunction<K, T, V> biF, BiConsumer<T, V> biC) {
        return keySetTraversal((k, c) -> process(c, k, biP, biF, biC));
    }

    @Override
    public <U> ICurveGroup<K, T, V> biProcess(ICurveGroup<K, U, V> cG, TriFunction<K, T, U, V> triF, BiConsumer<T, V> biC) {
        return keySetTraversal(cG, (k, c1, c2) -> biProcess(c1, c2, k, null, triF, biC));
    }

    @Override
    public <U> ICurveGroup<K, T, V> biProcess(ICurveGroup<K, U, V> cG, TriPredicate<K, T, U> triP, TriFunction<K, T, U, V> triF, BiConsumer<T, V> biC) {
        return keySetTraversal(cG, (k, c1, c2) -> biProcess(c1, c2, k, triP, triF, biC));
    }

    @Override
    public ICurveGroup<K, T, V> forCurve(BiConsumer<K, ICurve<T, V>> biC) {
        return keySetTraversal(biC);
    }

    @Override
    public <U> ICurveGroup<K, T, V> forCurve(ICurveGroup<K, U, V> cG, TriConsumer<K, ICurve<T, V>, ICurve<U, V>> triC) {
        return keySetTraversal(cG, triC);
    }

    public static <K, T, V> CurveGroup<K, T, V> create(List<T> l, Function<T, K> f) {
        return create(l.stream().collect(Collectors.groupingBy(f)));
    }

    public static <K, T, V> CurveGroup<K, T, V> create(Map<K, List<T>> cG) {
        CurveGroup<K, T, V> curve = new CurveGroup<>();
        for (K k : cG.keySet()) {
            curve.put(k, new Curve<>(cG.get(k)));
        }
        return curve;
    }
}
