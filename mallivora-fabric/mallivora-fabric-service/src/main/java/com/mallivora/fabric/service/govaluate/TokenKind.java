package com.mallivora.fabric.service.govaluate;

public class TokenKind {

    public static final int UNKNOWN  = 0;

    public static final int PREFIX = 1;
    public static final int NUMERIC = 2;
    public static final int BOOLEAN = 3;
    public static final int STRING = 4;
    public static final int PATTERN = 5;
    public static final int TIME = 6;
    public static final int VARIABLE = 7;
    public static final int  FUNCTION = 8;
    public static final int SEPARATOR = 9;

    public static final int COMPARATOR = 10;
    public static final int LOGICALOP = 11;
    public static final int MODIFIER = 12;

    public static final int CLAUSE = 13;
    public static final int CLAUSE_CLOSE = 14;

    public static final int TERNARY = 15;
}
