package com.chocoh.ql.core.curve;

import com.chocoh.ql.core.function.TriFunction;
import com.chocoh.ql.core.function.TriPredicate;
import com.chocoh.ql.core.function.TriConsumer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.*;
import java.util.stream.Collectors;

/**
 * @author chocoh
 */
public class CurveGroup<K, T, V> extends HashMap<K, ICurve<T, V>> implements ICurveGroup<K, T, V>{
    public static <K, T, V> CurveGroup<K, T, V> create(List<T> curve, Function< T,  K> keyF) {
        return curve == null ? null : createGroup(curve.stream().filter(Objects::nonNull).collect(Collectors.groupingBy(keyF)));
    }

    public ICurveGroup<K, T, V> process(BiConsumer< K, ICurve<T, V>> biC) {
        return keySetTraversal(biC);
    }

    public <U> ICurveGroup<K, T, V> biProcess(ICurveGroup<K, U, V> group, TriFunction< K,  T,  U,  V> triF, BiConsumer< T,  V> biC) {
        return keySetTraversal(group, (k, c1, c2) -> biProcess(c1, c2, k, null, triF, biC));
    }

    public <U> ICurveGroup<K, T, V> biProcess(ICurveGroup<K, U, V> group, TriPredicate< K,  T,  U> triP, TriFunction< K,  T,  U,  V> triF, BiConsumer< T,  V> biC) {
        return keySetTraversal(group, (k, c1, c2) -> biProcess(c1, c2, k, triP, triF, biC));
    }

    private <U> void biProcess(ICurve<T, V> curve1, ICurve<U, V> curve2, K k, TriPredicate<K, T, U> triP, TriFunction<K, T, U, V> triF, BiConsumer<T, V> biC) {
        curve1.biProcess(curve2, (c1, c2) -> triP.test(k, c1, c2), (c1, c2) -> triF.apply(k, c1, c2), biC);
    }

    private ICurveGroup<K, T, V> keySetTraversal(BiConsumer< K, ICurve<T, V>> biC) {
        for (K k : this.keySet()) {
            biC.accept(k, this.get(k));
        }
        return this;
    }

    private <U> ICurveGroup<K, T, V> keySetTraversal(ICurveGroup<K, U, V> group, TriConsumer<K, ICurve<T, V>, ICurve<U, V>> triC) {
        if (group != null) {
            for (K k : this.keySet()) {
                triC.accept(k, this.get(k), group.get(k));
            }
        }
        return this;
    }

    private static <K, T, V> CurveGroup<K, T, V> createGroup(Map<K, List<T>> group) {
        CurveGroup<K, T, V> curve = new CurveGroup<>();
        for (K k : group.keySet()) {
            curve.put(k, Curve.create(group.get(k)));
        }
        return curve;
    }
}
