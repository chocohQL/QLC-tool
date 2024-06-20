package com.chocoh.ql.processor;

import java.util.function.Consumer;

/**
 * @author chocoh
 */
@SuppressWarnings("UnusedReturnValue")
public interface IProcessorChain<T> {
    void processing(T t);

    IProcessorChain<T> removeProcessor(String name);

    IProcessorChain<T> addFirstProcessor(Consumer<T> c, String name);

    IProcessorChain<T> addLastProcessor(Consumer<T> c, String name);
}
