package com.mallivora.fabric.service.govaluate;

import static com.mallivora.fabric.service.govaluate.TokenKind.*;

public class LexerState {

    private boolean isEOF;
    private boolean isNUllAble;
    private int kind;
    private int[] validNextKinds;

    public static final LexerState[] validLexerStates = new LexerState[]{
            new LexerState(false, true, UNKNOWN, new int[]{PREFIX,
                    NUMERIC,
                    BOOLEAN,
                    VARIABLE,
                    PATTERN,
                    FUNCTION,
                    STRING,
                    TIME,
                    CLAUSE}),
            new LexerState(false, true, CLAUSE, new int[]{PREFIX,
                    NUMERIC,
                    BOOLEAN,
                    VARIABLE,
                    PATTERN,
                    FUNCTION,
                    STRING,
                    TIME,
                    CLAUSE,
                    CLAUSE_CLOSE}),
            new LexerState(true, true, CLAUSE_CLOSE, new int[]{COMPARATOR,
                    MODIFIER,
                    NUMERIC,
                    BOOLEAN,
                    VARIABLE,
                    STRING,
                    PATTERN,
                    TIME,
                    CLAUSE,
                    CLAUSE_CLOSE,
                    LOGICALOP,
                    TERNARY,
                    SEPARATOR}),
            new LexerState(true, false, NUMERIC, new int[]{MODIFIER,
                    COMPARATOR,
                    LOGICALOP,
                    CLAUSE_CLOSE,
                    TERNARY,
                    SEPARATOR}),
            new LexerState(true, false, BOOLEAN, new int[]{MODIFIER,
                    COMPARATOR,
                    LOGICALOP,
                    CLAUSE_CLOSE,
                    TERNARY,
                    SEPARATOR}),
            new LexerState(true, false, STRING, new int[]{MODIFIER,
                    COMPARATOR,
                    LOGICALOP,
                    CLAUSE_CLOSE,
                    TERNARY,
                    SEPARATOR}),
            new LexerState(true, false, TIME, new int[]{MODIFIER,
                    COMPARATOR,
                    LOGICALOP,
                    CLAUSE_CLOSE,
                    SEPARATOR}),
            new LexerState(true, false, PATTERN, new int[]{MODIFIER,
                    COMPARATOR,
                    LOGICALOP,
                    CLAUSE_CLOSE,
                    SEPARATOR}),
            new LexerState(true, false, VARIABLE, new int[]{MODIFIER,
                    COMPARATOR,
                    LOGICALOP,
                    CLAUSE_CLOSE,
                    TERNARY,
                    SEPARATOR}),
            new LexerState(false, false, MODIFIER, new int[]{PREFIX,
                    NUMERIC,
                    VARIABLE,
                    FUNCTION,
                    STRING,
                    BOOLEAN,
                    CLAUSE,
                    CLAUSE_CLOSE}),
            new LexerState(false, false, COMPARATOR, new int[]{PREFIX,
                    NUMERIC,
                    BOOLEAN,
                    VARIABLE,
                    FUNCTION,
                    STRING,
                    TIME,
                    CLAUSE,
                    CLAUSE_CLOSE,
                    PATTERN}),
            new LexerState(false, false, LOGICALOP, new int[]{PREFIX,
                    NUMERIC,
                    BOOLEAN,
                    VARIABLE,
                    FUNCTION,
                    STRING,
                    TIME,
                    CLAUSE,
                    CLAUSE_CLOSE}),
            new LexerState(false, false, PREFIX, new int[]{NUMERIC,
                    BOOLEAN,
                    VARIABLE,
                    FUNCTION,
                    CLAUSE,
                    CLAUSE_CLOSE}),
            new LexerState(false, false, TERNARY, new int[]{PREFIX,
                    NUMERIC,
                    BOOLEAN,
                    STRING,
                    TIME,
                    VARIABLE,
                    FUNCTION,
                    CLAUSE,
                    SEPARATOR}),
            new LexerState(false, false, FUNCTION, new int[]{CLAUSE}),
            new LexerState(false, true, SEPARATOR, new int[]{PREFIX,
                    NUMERIC,
                    BOOLEAN,
                    STRING,
                    TIME,
                    VARIABLE,
                    FUNCTION,
                    CLAUSE}),

    };

    public boolean isEOF() {
        return isEOF;
    }

    public void setEOF(boolean EOF) {
        isEOF = EOF;
    }

    public boolean isNUllAble() {
        return isNUllAble;
    }

    public void setNUllAble(boolean NUllAble) {
        isNUllAble = NUllAble;
    }

    public int getKind() {
        return kind;
    }

    public void setKind(int kind) {
        this.kind = kind;
    }

    public int[] getValidNextKinds() {
        return validNextKinds;
    }

    public void setValidNextKinds(int[] validNextKinds) {
        this.validNextKinds = validNextKinds;
    }

    public LexerState(boolean isEOF, boolean isNUllAble, int kind, int[] validNextKinds) {
        this.isEOF = isEOF;
        this.isNUllAble = isNUllAble;
        this.kind = kind;
        this.validNextKinds = validNextKinds;
    }

    public boolean canTransitionTo(int kind) {
        for (int validKind : this.validNextKinds) {
            if (validKind == kind) {
                return true;
            }
        }
        return false;
    }

    public LexerState getLexerStateForToken(int kind) {
        for (LexerState state : validLexerStates) {
            if (state.getKind() == kind) {
                return state;
            }
        }
        return validLexerStates[0];
    }
}
