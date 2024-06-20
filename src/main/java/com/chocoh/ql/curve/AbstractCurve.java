package com.chocoh.ql.curve;

import java.util.ArrayList;
import java.util.List;
import java.util.function.*;

/**
 * @author chocoh
 */
public abstract class AbstractCurve<T, V> extends ArrayList<T> implements ICurve<T, V>, MetaCurveOp<T, V> {
    public AbstractCurve() {
    }

    public AbstractCurve(List<T> l) {
        super(l);
    }

    @Override
    public void process(T t, Predicate<T> p, Function<T, V> f, BiConsumer<T, V> biC) {
        if (p == null || p.test(t)) {
            biC.accept(t, f.apply(t));
        }
    }

    @Override
    public <U> void biProcess(T t, U u, BiPredicate<T, U> biP, BiFunction<T, U, V> biF, BiConsumer<T, V> biC) {
        if (biP == null || biP.test(t, u)) {
            biC.accept(t, biF.apply(t, u));
        }
    }

    @Override
    public ICurve<T, V> traversal(Consumer<T> c) {
        if (this.size() > 0) {
            forEach(c);
        }
        return this;
    }

    @Override
    public <U> ICurve<T, V> biTraversal(ICurve<U, V> c, BiConsumer<T, U> biC) {
        if (this.size() > 0 && c != null && c.size() == this.size()) {
            for (int i = 0; i < this.size(); i++) {
                biC.accept(this.get(i), c.get(i));
            }
        }
        return this;
    }

    @Override
    public <U> ICurve<T, V> multiTraversal(List<ICurve<U, V>> cs, Consumer<ICurve<U, V>> c) {
        if (cs != null && !cs.isEmpty()) {
            for (int i = 0; i < cs.get(0).size(); i++) {
                c.accept(cs.get(i));
            }
        }
        return this;
    }
}
