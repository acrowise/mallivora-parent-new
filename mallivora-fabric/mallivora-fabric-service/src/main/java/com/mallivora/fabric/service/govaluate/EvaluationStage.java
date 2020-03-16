package com.mallivora.fabric.service.govaluate;


import org.apache.commons.lang3.ArrayUtils;
import com.mallivora.fabric.service.functionalInter.EvaluationOperator;
import com.mallivora.fabric.service.functionalInter.ExpressionFunction;

import static com.mallivora.fabric.service.govaluate.OperatorSymbol.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Stream;

public class EvaluationStage {

    private int symbol;
    private EvaluationStage leftStage;
    private EvaluationStage rightStage;
    private EvaluationOperator operator;
    private Function leftTypeCheck;
    private Function rightTypeCheck;
    private BiFunction<Object,Object,Boolean> typeCheck;
    private String typeErrorFormat;

    public EvaluationOperator getOperator() {
        return operator;
    }

    public void setOperator(EvaluationOperator operator) {
        this.operator = operator;
    }

    public Function getLeftTypeCheck() {
        return leftTypeCheck;
    }

    public void setLeftTypeCheck(Function leftTypeCheck) {
        this.leftTypeCheck = leftTypeCheck;
    }

    public Function getRightTypeCheck() {
        return rightTypeCheck;
    }

    public void setRightTypeCheck(Function rightTypeCheck) {
        this.rightTypeCheck = rightTypeCheck;
    }

    public BiFunction<Object,Object,Boolean> getTypeCheck() {
        return typeCheck;
    }

    public void setTypeCheck(BiFunction typeCheck) {
        this.typeCheck = typeCheck;
    }

    public String getTypeErrorFormat() {
        return typeErrorFormat;
    }

    public void setTypeErrorFormat(String typeErrorFormat) {
        this.typeErrorFormat = typeErrorFormat;
    }

    public int getSymbol() {
        return symbol;
    }

    public void setSymbol(int symbol) {
        this.symbol = symbol;
    }

    public EvaluationStage getLeftStage() {
        return leftStage;
    }

    public void setLeftStage(EvaluationStage leftStage) {
        this.leftStage = leftStage;
    }

    public EvaluationStage getRightStage() {
        return rightStage;
    }

    public void setRightStage(EvaluationStage rightStage) {
        this.rightStage = rightStage;
    }

    public static boolean comparatorTypeCheck(Object left, Object right){
        if (isFloat64(left) && isFloat64(right)){
            return true;
        }
        if (isString(left) && isString(right)){
            return true;
        }
        return false;
    }

    public static boolean isString(Object value){
        return "java.lang.String".equals(value.getClass().getTypeName());
    }

    public static boolean isRegexOrString(Object value){
        if("java.lang.String".equals(value.getClass().getTypeName()) || "java.util.regex.Pattern".equals(value.getClass().getTypeName())){
            return true;
        }
        return false;
    }

    public static boolean isBool(Object value){
        return "java.lang.Boolean".equals(value.getClass().getTypeName());
    }

    public static boolean isArray(Object value) {
        return value.getClass().isArray();
    }

    public static boolean isFloat64(Object value) {
        return "java.lang.Float".equals(value.getClass().getTypeName());
    }

    public static boolean additionTypeCheck(Object left,Object right){
        if (isFloat64(left) && isFloat64(right)) {
            return true;
        }
        if (!isString(left) && !isString(right)) {
            return false;
        }
        return true;
    }

    public void swapWith(EvaluationStage other) {
        EvaluationStage tmp = other;
        other.setToNonStage(this);
        this.setToNonStage(tmp);
    }

    public static EvaluationOperator makeLiteralStage(Object literal){
        return new EvaluationOperator() {
            @Override
            public Object apply(Object left, Object right, Object o3) {
                return literal;
            }
        };
    }

    public static EvaluationOperator makeFunctionStage(ExpressionFunction<Object,EvaluationOperator> function){
        return new EvaluationOperator() {
            @Override
            public Object apply(Object left, Object right, Object parameters) {
                if(null == right){
                    return function.apply();
                }
                if (right.getClass().isArray()) {
                    Object[] objects = Arrays.stream((Object[]) right).toArray();
                    return function.apply(objects);
                }
                return function.apply(right);
            }
        };
    }

    public static EvaluationOperator<Object,Object,Map<String,Object>,Object> makeParameterStage(String parameterName){

        return new EvaluationOperator<Object,Object,Map<String,Object>,Object>() {
            @Override
            public Object apply(Object left, Object right, Map map) {
                try{
                    return map.get(parameterName);
                } catch (NullPointerException ex){
                    return null;
                }
            }
        };
    }

    public static Boolean equalStage(Object left, Object right, Map<String,Object> parameters) {
        return left.equals(right);
    }

    public static Boolean notEqualStage(Object left, Object right, Map<String,Object> parameters) {
        return !left.equals(right);
    }


    public void setToNonStage(EvaluationStage other){
        this.symbol = other.getSymbol();
        this.operator = other.getOperator();
        this.leftTypeCheck = other.getLeftTypeCheck();
        this.rightTypeCheck = other.getRightTypeCheck();
        this.typeCheck = other.getTypeCheck();
        this.typeErrorFormat = other.getTypeErrorFormat();
    }

    public Boolean isShortCircuitAble(){
        if (this.symbol == AND ||
            this.symbol == OR ||
            this.symbol == TERNARY_TRUE ||
            this.symbol == TERNARY_FALSE ||
            this.symbol == COALESCE){
            return true;
        }
        return false;
    }

    public static Boolean gtStage(Object left, Object right, Map<String, Object> ObjectMap) {
        if(isString(left) && isString(right)){
            if(String.valueOf(left).compareTo(String.valueOf(right)) > 0 ){
                return true;
            } else {
                return false;
            }
        }
        if(isFloat64(left) && isFloat64(right)){
            return (Float)left > (Float)right;
        }
        throw new RuntimeException("right , left not match type { String, Float }");
    }

    public static boolean lteStage(Object left, Object right, Map<String, Object> map) {
        return !gtStage(left, right, map);

    }

    public static boolean gteStage(Object left, Object right, Map<String, Object> map) {
        return !ltStage(left, right, map);

    }

    public static boolean ltStage(Object left, Object right, Map<String, Object> map) {
        if(isString(left) && isString(right)){
            if(String.valueOf(left).compareTo(String.valueOf(right)) < 0 ){
                return true;
            } else {
                return false;
            }
        }
        if(isFloat64(left) && isFloat64(right)){
            return (Float)left < (Float)right;
        }
        throw new RuntimeException("right , left not match type { String, Float }");
    }

    public static Object regexStage(Object o, Object o1, Map<String, Object> map) {
        return null;
    }

    public static Object notRegexStage(Object o, Object o1, Map<String, Object> map) {
        return null;
    }

    public static Object separatorStage(Object left,Object right,Object o3){
        Object[] ret ;
        if(left.getClass().isArray()){
            ret = ArrayUtils.add((Object[])left,right);
        } else if (right.getClass().isArray() && !left.getClass().isArray()) {
            Object[] rights = (Object[])right;
            ret = new Object[1 + rights.length];
            ret[0] = left;
            for (int i = 0; i < rights.length; i++){
                ret[i + 1] = rights[i];
            }
        } else {
            ret = new Object[]{left,right};
        }

        return ret;
    }

    public static Object test(Object left , Object right) {
        Object[] ret ;
        if(left.getClass().isArray()){
            List<Object> objects = Arrays.asList((Object[]) left);
            objects.add(right);
            ret = objects.toArray(new Object[]{objects.size()});
        } else {
            ret = new Object[]{left,right};
        }
        return ret;
    }

    public static void main(String[] args) {
        System.out.println(test(new String[]{"1"},1).toString());
        Object[] o = new Object[]{1,"123"};
    }
}
