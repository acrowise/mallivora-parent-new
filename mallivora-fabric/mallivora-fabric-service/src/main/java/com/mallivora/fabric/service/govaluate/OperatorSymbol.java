package com.mallivora.fabric.service.govaluate;

import com.google.common.collect.ImmutableMap;
import java.util.HashMap;
import java.util.Map;

public class OperatorSymbol {

    public static final int VALUE = 0;
    public static final int LITERAL = 1;
    public static final int NOOP = 2;
    public static final int EQ = 3;
    public static final int NEQ = 4;
    public static final int GT = 5;
    public static final int LT = 6;
    public static final int GTE = 7;
    public static final int LTE = 8;
    public static final int REQ = 9;
    public static final int NREQ = 10;
    public static final int IN = 11;

    public static final int AND = 12;
    public static final int OR = 13;

    public static final int PLUS = 14;
    public static final int MINUS = 15;
    public static final int BITWISE_AND = 16;
    public static final int BITWISE_OR = 17;
    public static final int BITWISE_XOR = 18;
    public static final int BITWISE_LSHIFT = 19;
    public static final int BITWISE_RSHIFT = 20;
    public static final int MULTIPLY = 21;
    public static final int DIVIDE = 22;
    public static final int MODULUS = 23;
    public static final int EXPONENT = 24;

    public static final int NEGATE = 25;
    public static final int INVERT = 26;
    public static final int BITWISE_NOT = 27;

    public static final int TERNARY_TRUE = 28;
    public static final int TERNARY_FALSE = 29;
    public static final int COALESCE = 30;

    public static final int FUNCTIONAL = 31;
    public static final int SEPARATE = 32;

    public static final Map<String,Integer> prefixMap = ImmutableMap.of(
                                                            "-",NEGATE,
                                                            "!",INVERT,
                                                            "~",BITWISE_NOT);

    public static final Map<String,Integer> ternaryMap = ImmutableMap.of(
                                                            "?" ,  TERNARY_TRUE,
                                                            ":" ,  TERNARY_FALSE,
                                                            "??",  COALESCE);

    public static final Map<String,Integer> logicalMap = ImmutableMap.of(
            "&&" ,  AND,
            "||" ,  OR);

    public static final Map<String,Integer> modifierMap = new HashMap<String,Integer>() {
        {
            put("+", PLUS);
            put("-", MINUS);
            put("*", MULTIPLY);
            put("/", DIVIDE);
            put("%", MODULUS);
            put("**", EXPONENT);
            put("&", BITWISE_AND);
            put("|", BITWISE_OR);
            put("^", BITWISE_XOR);
            put(">>", BITWISE_RSHIFT);
            put("<<", BITWISE_LSHIFT);
        }
    };

    public static final Map<String,Integer> comparatorMap = new HashMap<String,Integer>() {
        {
            put("==", EQ);
            put("!=", NEQ);
            put(">",  GT);
            put(">=", GTE);
            put("<",  LT);
            put("<=", LTE);
            put("=~", REQ);
            put("!~", NREQ);
            put("in", IN);
        }
    };

    public static final Map<String,Integer> separatorMap = new HashMap<String,Integer>() {
        {
            put(",",SEPARATE);
        }
    };

    public static final Map<String,Integer> exponentialMap = new HashMap<String,Integer>() {
        {
            put("**",EXPONENT);
        }
    };
    public static final Map<String,Integer> multiplicativeMap = new HashMap<String,Integer>() {
        {
            put("*",MULTIPLY);
            put("/",DIVIDE);
            put("%",MODULUS);
        }
    };
    public static final Map<String,Integer> additiveMap = new HashMap<String,Integer>() {
        {
            put("+",PLUS);
            put("-",MINUS);
        }
    };
    public static final Map<String,Integer> bitwiseShiftMap = new HashMap<String,Integer>() {
        {
            put(">>",BITWISE_RSHIFT);
            put("<<",BITWISE_LSHIFT);
        }
    };

    public static final Map<String,Integer> bitwiseMap = new HashMap<String,Integer>() {
        {
            put("^",BITWISE_XOR);
            put("&",BITWISE_AND);
            put("|",BITWISE_OR);
        }
    };



    public static boolean prefixSymbols(String tokenString){
        return prefixMap.containsKey(tokenString);
    }

    public static boolean ternarySymbols(String tokenString){
        return ternaryMap.containsKey(tokenString);
    }

    public static boolean logicalSymbols(String tokenString){
        return logicalMap.containsKey(tokenString);
    }

    public static boolean modifierSymbols(String tokenString){
        return modifierMap.containsKey(tokenString);
    }

    public static boolean comparatorSymbols(String tokenString){
        return comparatorMap.containsKey(tokenString);
    }
/*
    public static Map<String, Integer> getPrefixMap() {
        return prefixMap;
    }

    public static Map<String, Integer> getExponentialMap() {
        return exponentialMap;
    }

    public static Map<String, Integer> getMultiplicativeMap() {
        return multiplicativeMap;
    }
    public static Map<String, Integer> getAdditiveMap() {
        return additiveMap;
    }

    public static Map<String, Integer> getTernaryMap() {
        return ternaryMap;
    }

    public static Map<String, Integer> getComparatorMap() {
        return comparatorMap;
    }

    public static Map<String, Integer> getLogicalMap() {
        return logicalMap;
    }

    public static Map<String, Integer> getModifierMap() {
        return modifierMap;
    }*/

    class OperatorPrecedence {
        public static final  int noopPrecedence = 0;
        public static final  int valuePrecedence = 1;
        public static final  int functionalPrecedence = 2;
        public static final  int prefixPrecedence = 3;
        public static final  int exponentialPrecedence = 4;
        public static final  int additivePrecedence = 5;
        public static final  int bitwisePrecedence = 6;
        public static final  int bitwiseShiftPrecedence = 7;
        public static final  int multiplicativePrecedence = 8;
        public static final  int comparatorPrecedence = 9;
        public static final  int ternaryPrecedence = 10;
        public static final  int logicalAndPrecedence = 11;
        public static final  int logicalOrPrecedence = 12;
        public static final  int separatePrecedence = 13;
    }

    public static int findOperatorPrecedenceForSymbol(int symbol){
        switch (symbol){
            case NOOP:
                return OperatorPrecedence.noopPrecedence;
            case VALUE:
                return OperatorPrecedence.valuePrecedence;
            case EQ:
            case NEQ:
            case GT:
            case LT:
            case GTE:
            case LTE:
            case REQ:
            case NREQ:
            case IN:
                return OperatorPrecedence.comparatorPrecedence;
            case AND:
                return OperatorPrecedence.logicalAndPrecedence;
            case OR:
                return OperatorPrecedence.logicalOrPrecedence;
            case BITWISE_AND:
            case BITWISE_OR:
            case BITWISE_XOR:
                return OperatorPrecedence.bitwisePrecedence;
            case BITWISE_LSHIFT:
            case BITWISE_RSHIFT:
                return OperatorPrecedence.bitwiseShiftPrecedence;
            case PLUS:
            case MINUS:
                return OperatorPrecedence.additivePrecedence;
            case MULTIPLY:
            case DIVIDE:
            case MODULUS:
                return OperatorPrecedence.multiplicativePrecedence;
            case EXPONENT:
                return OperatorPrecedence.exponentialPrecedence;
            case BITWISE_NOT:
            case NEGATE:
            case INVERT:
                return OperatorPrecedence.prefixPrecedence;
            case COALESCE:
            case TERNARY_TRUE:
            case TERNARY_FALSE:
                return OperatorPrecedence.ternaryPrecedence;
            case FUNCTIONAL:
                return OperatorPrecedence.functionalPrecedence;
            case SEPARATE:
                return OperatorPrecedence.separatePrecedence;
        }

        return OperatorPrecedence.valuePrecedence;
    }

    public static Boolean test(int i ,String s ,char ch){
        return false;
    }




}
