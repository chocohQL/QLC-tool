package com.chocoh.ql.processor;

/**
 * @author chocoh
 */
public interface IProcessor<T> {
    void processing(T t);

    String getName();
}
