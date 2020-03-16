package com.mallivora.fabric.service.functionalInter;

@FunctionalInterface
public interface ExpressionFunction<T,B> {

    B apply(T... t);
}
