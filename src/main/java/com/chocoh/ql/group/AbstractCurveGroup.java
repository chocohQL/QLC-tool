package com.chocoh.ql.group;

import com.chocoh.ql.curve.ICurve;
import com.chocoh.ql.function.TriConsumer;
import com.chocoh.ql.function.TriFunction;
import com.chocoh.ql.function.TriPredicate;

import java.util.HashMap;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;

/**
 * 抽象分组曲线
 *
 * <p> AbstractCurveGroup 实现 分组曲线 元操作方法, 默认增强 HashMap </p>
 * <p> • 接口 {@link com.chocoh.ql.group.MetaGroupOp} 定义分组曲线元操作, 默认简单实现, 子类可按需重写</p>
 * <p> • 接口 {@link com.chocoh.ql.group.ICurveGroup} 定义分组曲线模板, 留给子类实现</p>
 *
 * @author chocoh
 */
public abstract class AbstractCurveGroup<K, T, V> extends HashMap<K, ICurve<T, V>> implements ICurveGroup<K, T, V>, MetaGroupOp<K, T, V> {
    @Override
    public void process(ICurve<T, V> c, K k, BiConsumer<K, T> biC) {
        c.process(t -> biC.accept(k, t));
    }

    @Override
    public void process(ICurve<T, V> c, K key, BiPredicate<K, T> biP, BiFunction<K, T, V> biF, BiConsumer<T, V> biC) {
        c.process(t -> biP == null || biP.test(key, t), t -> biF.apply(key, t), biC);
    }

    @Override
    public <U> void biProcess(ICurve<T, V> c1, ICurve<U, V> c2, K k, TriPredicate<K, T, U> triP, TriFunction<K, T, U, V> triF, BiConsumer<T, V> biC) {
        c1.biProcess(c2, (t, u) -> triP == null || triP.test(k, t, u), (t, u) -> triF.apply(k, t, u), biC);
    }

    @Override
    public ICurveGroup<K, T, V> keySetTraversal(BiConsumer<K, ICurve<T, V>> biC) {
        for (K k : this.keySet()) {
            biC.accept(k, this.get(k));
        }
        return this;
    }

    @Override
    public <U> ICurveGroup<K, T, V> keySetTraversal(ICurveGroup<K, U, V> cG, TriConsumer<K, ICurve<T, V>, ICurve<U, V>> triC) {
        if (cG != null) {
            for (K k : this.keySet()) {
                triC.accept(k, this.get(k), cG.get(k));
            }
        }
        return this;
    }
}
