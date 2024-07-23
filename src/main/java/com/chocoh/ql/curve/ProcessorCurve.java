package com.chocoh.ql.curve;

import com.chocoh.ql.processor.ProcessorChain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.*;

/**
 * 处理器增强曲线
 *
 * <p> • ProcessorCurve 为 {@link com.chocoh.ql.curve.Curve} 的增强, 提供了前后置处理器链, 你可以添加多个处理器,
 * 数据处理器在每一次数据处理前后触发, 拿到的是曲线数据; 曲线处理器在一次曲线处理前后触发, 拿到的是曲线本身. </p>
 * <p> • ProcessorCurve 仅对 {@link com.chocoh.ql.curve.MetaCurveOp} 元曲线操作方法做了简单的重写, 需要手动添加删除处理器,
 * 会产生一定的性能损耗, 建议只作为调试工具使用. </p>
 *
 * @author chocoh
 */
public class ProcessorCurve<T, V> extends Curve<T, V> {
    private ProcessorChain<T> preDataChain;
    private ProcessorChain<T> postDataChain;
    private ProcessorChain<ICurve<T, V>> preCurveChain;
    private ProcessorChain<ICurve<T, V>> postCurveChain;
    private boolean enableDataProcessor = true;
    private boolean enableCurveProcessor = true;

    public ProcessorCurve() {
    }

    public ProcessorCurve(List<T> l) {
        super(l);
    }

    private void dataChain(ProcessorChain<T> p, T t) {
        if (p != null && enableDataProcessor) {
            p.processing(t);
        }
    }

    private void curveChain(ProcessorChain<ICurve<T, V>> p, ICurve<T, V> c) {
        if (p != null && enableCurveProcessor) {
            p.processing(c);
        }
    }

    @Override
    public void process(T t, Predicate<T> p, Function<T, V> f, BiConsumer<T, V> biC) {
        if (p == null || p.test(t)) {
            dataChain(preDataChain, t);
            biC.accept(t, f.apply(t));
            dataChain(postDataChain, t);
        }
    }

    @Override
    public <U> void biProcess(T t, U u, BiPredicate<T, U> biP, BiFunction<T, U, V> biF, BiConsumer<T, V> biC) {
        if (biP == null || biP.test(t, u)) {
            dataChain(preDataChain, t);
            biC.accept(t, biF.apply(t, u));
            dataChain(postDataChain, t);
        }
    }

    @Override
    public ICurve<T, V> traversal(Consumer<T> c) {
        if (this.size() > 0) {
            curveChain(preCurveChain, this);
            forEach(c);
            curveChain(postCurveChain, this);
        }
        return this;
    }

    @Override
    public <U> ICurve<T, V> biTraversal(ICurve<U, V> c, BiConsumer<T, U> biC) {
        if (this.size() > 0 && c != null && c.size() == this.size()) {
            curveChain(preCurveChain, this);
            for (int i = 0; i < this.size(); i++) {
                biC.accept(this.get(i), c.get(i));
            }
            curveChain(postCurveChain, this);
        }
        return this;
    }

    public boolean isEnableDataProcessor() {
        return enableDataProcessor;
    }

    public void setEnableDataProcessor(boolean enableDataProcessor) {
        this.enableDataProcessor = enableDataProcessor;
    }

    public boolean isEnableCurveProcessor() {
        return enableCurveProcessor;
    }

    public void setEnableCurveProcessor(boolean enableCurveProcessor) {
        this.enableCurveProcessor = enableCurveProcessor;
    }

    public ProcessorChain<T> getPreDataChain() {
        return preDataChain;
    }

    public void setPreDataChain(ProcessorChain<T> preDataChain) {
        this.preDataChain = preDataChain;
    }

    public ProcessorChain<T> getPostDataChain() {
        return postDataChain;
    }

    public void setPostDataChain(ProcessorChain<T> postDataChain) {
        this.postDataChain = postDataChain;
    }

    public ProcessorChain<ICurve<T, V>> getPreCurveChain() {
        return preCurveChain;
    }

    public void setPreCurveChain(ProcessorChain<ICurve<T, V>> preCurveChain) {
        this.preCurveChain = preCurveChain;
    }

    public ProcessorChain<ICurve<T, V>> getPostCurveChain() {
        return postCurveChain;
    }

    public void setPostCurveChain(ProcessorChain<ICurve<T, V>> postCurveChain) {
        this.postCurveChain = postCurveChain;
    }

    public static class Builder<T, V> {
        private Collection<T> l = new ArrayList<>();
        private ProcessorChain<T> preDataChain = new ProcessorChain<>();
        private ProcessorChain<T> postDataChain = new ProcessorChain<>();
        private ProcessorChain<ICurve<T, V>> preCurveChain = new ProcessorChain<>();
        private ProcessorChain<ICurve<T, V>> postCurveChain = new ProcessorChain<>();
        private boolean enableDataProcessor = true;
        private boolean enableCurveProcessor = true;

        public Builder() {
        }

        public Builder<T, V> data(Collection<T> l) {
            this.l = l;
            return this;
        }

        public Builder<T, V> preDataProcessor(Consumer<T> c, String name) {
            preDataChain.addFirstProcessor(c, name);
            return this;
        }

        public Builder<T, V> postDataProcessor(Consumer<T> c, String name) {
            postDataChain.addLastProcessor(c, name);
            return this;
        }

        public Builder<T, V> preCurveProcessor(Consumer<ICurve<T, V>> c, String name) {
            preCurveChain.addFirstProcessor(c, name);
            return this;
        }

        public Builder<T, V> postCurveProcessor(Consumer<ICurve<T, V>> c, String name) {
            postCurveChain.addLastProcessor(c, name);
            return this;
        }

        public Builder<T, V> enableDataProcessor(boolean enableDataProcessor) {
            this.enableDataProcessor = enableDataProcessor;
            return this;
        }

        public Builder<T, V> enableCurveProcessor(boolean enableCurveProcessor) {
            this.enableCurveProcessor = enableCurveProcessor;
            return this;
        }

        public Curve<T, V> build() {
            ProcessorCurve<T, V> curve = new ProcessorCurve<>();
            if (this.l != null) {
                curve.addAll(l);
            }
            if (this.preDataChain != null) {
                curve.preDataChain = this.preDataChain;
            }
            if (this.postDataChain != null) {
                curve.postDataChain = this.postDataChain;
            }
            if (this.preCurveChain != null) {
                curve.preCurveChain = this.preCurveChain;
            }
            if (this.postCurveChain != null) {
                curve.postCurveChain = this.postCurveChain;
            }
            curve.enableDataProcessor = this.enableDataProcessor;
            curve.enableCurveProcessor = this.enableCurveProcessor;
            return curve;
        }

        public Collection<T> getL() {
            return l;
        }

        public void setL(Collection<T> l) {
            this.l = l;
        }

        public ProcessorChain<T> getPreDataChain() {
            return preDataChain;
        }

        public void setPreDataChain(ProcessorChain<T> preDataChain) {
            this.preDataChain = preDataChain;
        }

        public ProcessorChain<T> getPostDataChain() {
            return postDataChain;
        }

        public void setPostDataChain(ProcessorChain<T> postDataChain) {
            this.postDataChain = postDataChain;
        }

        public ProcessorChain<ICurve<T, V>> getPreCurveChain() {
            return preCurveChain;
        }

        public void setPreCurveChain(ProcessorChain<ICurve<T, V>> preCurveChain) {
            this.preCurveChain = preCurveChain;
        }

        public ProcessorChain<ICurve<T, V>> getPostCurveChain() {
            return postCurveChain;
        }

        public void setPostCurveChain(ProcessorChain<ICurve<T, V>> postCurveChain) {
            this.postCurveChain = postCurveChain;
        }

        public boolean isEnableDataProcessor() {
            return enableDataProcessor;
        }

        public boolean isEnableCurveProcessor() {
            return enableCurveProcessor;
        }
    }
}
