package com.mallivora.fabric.service.functionalInter;

@FunctionalInterface
public interface EvaluationOperator<T,U,R,Z> {
    Z apply(T t,U u,R r);
}
