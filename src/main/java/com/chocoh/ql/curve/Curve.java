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
            processor(preProcessor, t);
            biC.accept(t, f.apply(t));
            processor(postProcessor, t);
        }
    }

    private <U> void biProcess(T t, U u, BiPredicate<T, U> biP, BiFunction<T, U, V> biF, BiConsumer<T, V> biC) {
        if (biP == null || biP.test(t, u)) {
            processor(preProcessor, t);
            biC.accept(t, biF.apply(t, u));
            processor(postProcessor, t);
        }
    }

    private Curve<T, V> traversal(Consumer<T> c) {
        processor(globalPreProcessor, this);
        forEach(c);
        processor(globalPostProcessor, this);
        return this;
    }

    private <U> Curve<T, V> biTraversal(ICurve<U, V> c, BiConsumer<T, U> biC) {
        if (c != null && c.size() == this.size()) {
            processor(globalPreProcessor, this);
            for (int i = 0; i < this.size(); i++) {
                biC.accept(this.get(i), c.get(i));
            }
            processor(globalPostProcessor, this);
        }
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

    private Consumer<T> preProcessor;

    private Consumer<T> postProcessor;

    private Consumer<ICurve<T, V>> globalPreProcessor;

    private Consumer<ICurve<T, V>> globalPostProcessor;

    private void processor(Consumer<T> c, T t) {
        if (c != null) {
            c.accept(t);
        }
    }

    private void processor(Consumer<ICurve<T, V>> c, ICurve<T, V> curve) {
        if (c != null) {
            c.accept(curve);
        }
    }

    public Curve<T, V> preProcessor(Consumer<T> c) {
        this.preProcessor = c;
        return this;
    }

    public Curve<T, V> postProcessor(Consumer<T> c) {
        this.postProcessor = c;
        return this;
    }

    public Curve<T, V> globalPreProcessor(Consumer<ICurve<T, V>> c) {
        this.globalPreProcessor = c;
        return this;
    }

    public Curve<T, V> globalPostProcessor(Consumer<ICurve<T, V>> c) {
        this.globalPostProcessor = c;
        return this;
    }
}
