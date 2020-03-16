package com.mallivora.fabric.service.govaluate;

public class ExpressionToken {

    private int Kind;

    private Object Value;

    public int getKind() {
        return Kind;
    }

    public void setKind(int kind) {
        Kind = kind;
    }

    public Object getValue() {
        return Value;
    }

    public void setValue(Object value) {
        Value = value;
    }


}
