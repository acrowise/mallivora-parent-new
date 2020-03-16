package com.mallivora.fabric.service.govaluate;

import java.util.function.BiFunction;
import java.util.function.Function;


public class TypeChecks {

    private Function left;
    private Function right;
    private BiFunction<Function,Function,Boolean> combined;

    public Function getLeft() {
        return left;
    }

    public void setLeft(Function left) {
        this.left = left;
    }

    public Function getRight() {
        return right;
    }

    public void setRight(Function right) {
        this.right = right;
    }

    public BiFunction<Function, Function, Boolean> getCombined() {
        return combined;
    }

    public void setCombined(BiFunction<Function, Function, Boolean> combined) {
        this.combined = combined;
    }


}
