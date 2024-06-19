package com.chocoh.ql.curve;

import java.util.ArrayList;
import java.util.List;
import java.util.function.*;

/**
 * @author chocoh
 */
public class Curve<T, V> extends ArrayList<T> implements ICurve<T, V> {
    public Curve(List<T> l) {
        super(l);
    }

    public static <T, V> ICurve<T, V> create(List<T> l) {
        return new Curve<>(l);
    }

    public ICurve<T, V> process(Consumer<T> c) {
        return traversal(c);
    }

    public ICurve<T, V> process(Function<T, V> f, BiConsumer<T, V> biC) {
        return traversal(t -> process(t, null, f, biC));
    }

    public ICurve<T, V> process(Predicate<T> p, Function<T, V> f, BiConsumer<T, V> biC) {
        return traversal(t -> process(t, p, f, biC));
    }

    public <U> ICurve<T, V> biProcess(ICurve<U, V> curve, BiFunction<T, U, V> biF, BiConsumer<T, V> biC) {
        return biTraversal(curve, (t, u) -> biProcess(t, u, null, biF, biC));
    }

    public <U> ICurve<T, V> biProcess(ICurve<U, V> curve, BiPredicate<T, U> biP, BiFunction<T, U, V> biF, BiConsumer<T, V> biC) {
        return biTraversal(curve, (t, u) -> biProcess(t, u, biP, biF, biC));
    }

    public <U> ICurve<T, V> multiProcess(List<ICurve<U, V>> curves, BiFunction<T, U, V> biF, BiConsumer<T, V> biC) {
        return multiTraversal(curves, c -> biProcess(c, null, biF, biC));
    }

    public <U> ICurve<T, V> multiProcess(List<ICurve<U, V>> curves, BiPredicate<T, U> biP, BiFunction<T, U, V> biF, BiConsumer<T, V> biC) {
        return multiTraversal(curves, c -> biProcess(c, biP, biF, biC));
    }

    private void process(T t, Predicate<T> p, Function<T, V> f, BiConsumer<T, V> biC) {
        if (p == null || p.test(t)) {
            V v = f.apply(t);
            biC.accept(t, v);
        }
    }

    private <U> void biProcess(T t, U u, BiPredicate<T, U> biP, BiFunction<T, U, V> biF, BiConsumer<T, V> biC) {
        if (biP == null || biP.test(t, u)) {
            V v = biF.apply(t, u);
            biC.accept(t, v);
        }
    }

    private Curve<T, V> traversal(Consumer<T> c) {
        this.forEach(c);
        return this;
    }

    private <U> Curve<T, V> multiTraversal(List<ICurve<U, V>> cs, Consumer<ICurve<U, V>> c) {
        if (cs != null && !cs.isEmpty()) {
            for (int i = 0; i < cs.get(0).size(); i++) {
                c.accept(cs.get(i));
            }
        }
        return this;
    }

    private <U> Curve<T, V> biTraversal(ICurve<U, V> c, BiConsumer<T, U> biC) {
        if (c != null && c.size() == this.size()) {
            for (int i = 0; i < this.size(); i++) {
                biC.accept(this.get(i), c.get(i));
            }
        }
        return this;
    }
}
