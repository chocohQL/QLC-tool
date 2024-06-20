package com.chocoh.ql.processor;

import java.util.LinkedList;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * @author chocoh
 */
public class ProcessorChain<T> implements IProcessorChain<T> {
    private final LinkedList<IProcessor<T>> chain = new LinkedList<>();

    @Override
    public void processing(T t) {
        chain.forEach(p -> p.processing(t));
    }

    @Override
    public IProcessorChain<T> addFirstProcessor(Consumer<T> c, String name) {
        chain.addFirst(new Processor<>(c, name));
        return this;
    }

    @Override
    public IProcessorChain<T> addLastProcessor(Consumer<T> c, String name) {
        chain.addLast(new Processor<>(c, name));
        return this;
    }

    @Override
    public IProcessorChain<T> removeProcessor(String name) {
        for (IProcessor<T> p : chain) {
            if (Objects.equals(p.getName(), name)) {
                chain.remove(p);
                return this;
            }
        }
        return this;
    }

    static class Processor<T> implements IProcessor<T> {
        private final Consumer<T> c;
        private final String name;

        public Processor(Consumer<T> c, String name) {
            this.c = c;
            this.name = name;
        }

        @Override
        public void processing(T t) {
            c.accept(t);
        }

        @Override
        public String getName() {
            return name;
        }
    }
}
