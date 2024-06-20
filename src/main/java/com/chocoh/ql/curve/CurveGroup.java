package com.chocoh.ql.curve;

import com.chocoh.ql.function.TriFunction;
import com.chocoh.ql.function.TriPredicate;
import com.chocoh.ql.function.TriConsumer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.*;
import java.util.stream.Collectors;

/**
 * @author chocoh
 */
public class CurveGroup<K, T, V> extends HashMap<K, ICurve<T, V>> implements ICurveGroup<K, T, V> {
    public static <K, T, V> CurveGroup<K, T, V> create(List<T> l, Function<T, K> f) {
        return create(l.stream().collect(Collectors.groupingBy(f)));
    }

    @Override
    public ICurveGroup<K, T, V> process(BiConsumer<K, T> biC) {
        return keySetTraversal((k, c) -> process(c, k, biC));
    }

    @Override

    public ICurveGroup<K, T, V> process(BiFunction<K, T, V> biF, BiConsumer<T, V> biC) {
        return keySetTraversal((k, c) -> process(c, k, null, biF, biC));
    }

    @Override

    public ICurveGroup<K, T, V> process(BiFunction<K, T, V> biF, BiPredicate<K, T> biP, BiConsumer<T, V> biC) {
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
    public <U> ICurveGroup<K, T, V> forCurve(ICurveGroup<K, U, V> cG, TriConsumer<K, ICurve<T, V>, ICurve<U, V>> biC) {
        return keySetTraversal(cG, biC);
    }

    private void process(ICurve<T, V> c, K k, BiConsumer<K, T> biC) {
        c.process(t -> biC.accept(k, t));
    }

    private void process(ICurve<T, V> c, K key, BiPredicate<K, T> biP, BiFunction<K, T, V> biF, BiConsumer<T, V> biC) {
        c.process(t -> biP == null || biP.test(key, t), t -> biF.apply(key, t), biC);
    }

    private <U> void biProcess(ICurve<T, V> c1, ICurve<U, V> c2, K k, TriPredicate<K, T, U> triP, TriFunction<K, T, U, V> triF, BiConsumer<T, V> biC) {
        c1.biProcess(c2, (t, u) -> triP == null || triP.test(k, t, u), (t, u) -> triF.apply(k, t, u), biC);
    }

    private ICurveGroup<K, T, V> keySetTraversal(BiConsumer<K, ICurve<T, V>> biC) {
        for (K k : this.keySet()) {
            biC.accept(k, this.get(k));
        }
        return this;
    }

    private <U> ICurveGroup<K, T, V> keySetTraversal(ICurveGroup<K, U, V> cG, TriConsumer<K, ICurve<T, V>, ICurve<U, V>> triC) {
        if (cG != null) {
            for (K k : this.keySet()) {
                triC.accept(k, this.get(k), cG.get(k));
            }
        }
        return this;
    }

    private static <K, T, V> CurveGroup<K, T, V> create(Map<K, List<T>> cG) {
        CurveGroup<K, T, V> curve = new CurveGroup<>();
        for (K k : cG.keySet()) {
            curve.put(k, Curve.create(cG.get(k)));
        }
        return curve;
    }
}
