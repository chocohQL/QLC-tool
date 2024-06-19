package com.chocoh.ql.core.curve;

import java.util.List;
import java.util.function.*;

/**
 * @author chocoh
 */
public class Curve<T, V> implements ICurve<T, V> {
    private List<T> data;
    private Consumer<V> postProcessor;

    public static <T, V> Curve<T, V> createCurve(List<T> data) {
        return new Curve<>(data);
    }

    public Curve<T, V> process(Consumer<T> c) {
        return traversal(c);
    }

    public Curve<T, V> process(Function<T, V> f, BiConsumer<T, V> biC) {
        return traversal(t -> process(t, null, f, biC));
    }

    public Curve<T, V> process(Predicate<T> p, Function<T, V> f, BiConsumer<T, V> biC) {
        return traversal(t -> process(t, p, f, biC));
    }

    public <U> Curve<T, V> biProcess(ICurve<U, V> curve, BiFunction<T, U, V> biF, BiConsumer<T, V> biC) {
        return biTraversal(curve, (t, u) -> biProcess(t, u, null, biF, biC));
    }

    public <U> Curve<T, V> biProcess(ICurve<U, V> curve, BiPredicate<T, U> biP, BiFunction<T, U, V> biF, BiConsumer<T, V> biC) {
        return biTraversal(curve, (t, u) -> biProcess(t, u, biP, biF, biC));
    }

    public <U> Curve<T, V> multiProcess(List<ICurve<U, V>> curves, BiFunction<T, U, V> biF, BiConsumer<T, V> biC) {
        return multiTraversal(curves, c -> biProcess(c, null, biF, biC));
    }

    public <U> Curve<T, V> multiProcess(List<ICurve<U, V>> curves, BiPredicate<T, U> biP, BiFunction<T, U, V> biF, BiConsumer<T, V> biC) {
        return multiTraversal(curves, c -> biProcess(c, biP, biF, biC));
    }

    public Curve<T, V> mergeCurve(ICurve<T, V> curve) {
        return merge(curve);
    }

    public Curve<T, V> mergeCurve(List<ICurve<T, V>> curves) {
        return multiTraversal(curves, this::merge);
    }

    private Curve(List<T> data) {
        this.data = data;
    }

    private void postProcessing(V v) {
        if (postProcessor != null) {
            postProcessor.accept(v);
        }
    }

    private void process(T t, Predicate<T> p, Function<T, V> f, BiConsumer<T, V> biC) {
        if (p == null || p.test(t)) {
            V v = f.apply(t);
            biC.accept(t, v);
            postProcessing(v);
        }
    }

    private <U> void biProcess(T t, U u, BiPredicate<T, U> biP, BiFunction<T, U, V> biF, BiConsumer<T, V> biC) {
        if (biP == null || biP.test(t, u)) {
            V v = biF.apply(t, u);
            biC.accept(t, v);
            postProcessing(v);
        }
    }

    private Curve<T, V> traversal(Consumer<T> c) {
        for (T t : this.data) {
            c.accept(t);
        }
        return this;
    }

    private <U> Curve<T, V> multiTraversal(List<ICurve<U, V>> curves, Consumer<ICurve<U, V>> c) {
        if (curves != null && !curves.isEmpty()) {
            for (int i = 0; i < curves.get(0).getData().size(); i++) {
                c.accept(curves.get(i));
            }
        }
        return this;
    }

    private <U> Curve<T, V> biTraversal(ICurve<U, V> curve, BiConsumer<T, U> biC) {
        if (curve != null && curve.getData().size() == this.data.size()) {
            for (int i = 0; i < this.data.size(); i++) {
                biC.accept(this.data.get(i), curve.getData().get(i));
            }
        }
        return this;
    }

    private Curve<T, V> merge(ICurve<T, V> curve) {
        if (curve != null) {
            this.getData().addAll(curve.getData());
        }
        return this;
    }

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }

    public Consumer<V> getPostProcessor() {
        return postProcessor;
    }

    public void setPostProcessor(Consumer<V> postProcessor) {
        this.postProcessor = postProcessor;
    }
}
