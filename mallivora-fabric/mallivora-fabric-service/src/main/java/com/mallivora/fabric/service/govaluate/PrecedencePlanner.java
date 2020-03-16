package com.mallivora.fabric.service.govaluate;

import java.util.Map;
import java.util.function.Function;

public class PrecedencePlanner {
    private Map<String,Integer> validSymbols;
    private int[] validKinds;
    private String typeErrorFormat;
    private Function<TokenStream,EvaluationStage> next;
    private Function<TokenStream,EvaluationStage> nextRight;
}
