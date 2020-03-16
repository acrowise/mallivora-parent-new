package com.mallivora.fabric.service.govaluate;

import org.apache.commons.lang3.ArrayUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Pattern;

import static com.mallivora.fabric.service.govaluate.OperatorSymbol.*;
import static com.mallivora.fabric.service.govaluate.TokenKind.*;

public class EvaluableExpression {

    private static final String ISO_DATE_FORMAT = "2006-01-02T15:04:05.999999999Z0700";
    private static final Integer SHORT_CIRCUIT_HOLDER = -1;

    private String QueryDateFormat;
    private boolean ChecksTypes;
    private ExpressionToken[] tokens;
    private String inputExpression;
    private EvaluationStage evaluationStages;

    public String getQueryDateFormat() {
        return QueryDateFormat;
    }

    public void setQueryDateFormat(String queryDateFormat) {
        QueryDateFormat = queryDateFormat;
    }

    public boolean isChecksTypes() {
        return ChecksTypes;
    }

    public void setChecksTypes(boolean checksTypes) {
        ChecksTypes = checksTypes;
    }

    public ExpressionToken[] getTokens() {
        return tokens;
    }

    public void setTokens(ExpressionToken[] tokens) {
        this.tokens = tokens;
    }

    public String getInputExpression() {
        return inputExpression;
    }

    public void setInputExpression(String inputExpression) {
        this.inputExpression = inputExpression;
    }

    public EvaluationStage getEvaluationStages() {
        return evaluationStages;
    }

    public void setEvaluationStages(EvaluationStage evaluationStages) {
        this.evaluationStages = evaluationStages;
    }

    public static EvaluableExpression newEvaluableExpressionWithFunctions(String expression, Map functions){
        EvaluableExpression ret = new EvaluableExpression();
        ret.setQueryDateFormat(ISO_DATE_FORMAT);
        ret.setInputExpression(expression);
        ExpressionToken[] expressionTokens = Parsing.parseTokens(expression, functions);
        ret.setTokens(expressionTokens);
        ret.setTokens(optimizeTokens(ret.getTokens()));
        ret.setEvaluationStages(StagePlanner.planStages(ret.getTokens()));
        ret.setChecksTypes(true);
        return ret;
    }

    private Object evaluateStage(EvaluationStage stage, Map<String,Object> map){

        Object left = null, right = null;

        if (null != stage.getLeftStage()){
            left = this.evaluateStage(stage.getLeftStage(),map);
        }

        if(stage.isShortCircuitAble()){
            switch (stage.getSymbol()){
                case AND:
                    if ((Boolean) left == false) {
                        return false;
                    }
                    break;
                case OR:
                    if ((Boolean) right == true) {
                        return true;
                    }
                    break;
                case COALESCE:
                    if (null != left) {
                        return left;
                    }
                    break;
                case TERNARY_TRUE:
                    if ((Boolean) left == false){
                        right = SHORT_CIRCUIT_HOLDER;
                    }
                    break;
                case TERNARY_FALSE:
                    if (left != null) {
                        right = SHORT_CIRCUIT_HOLDER;
                    }
            }
        }
        if (right != SHORT_CIRCUIT_HOLDER && null != stage.getRightStage()) {
            right = this.evaluateStage(stage.getRightStage(),map);
        }

        if (this.ChecksTypes) {
            if (null == stage.getTypeCheck()) {
                typeCheck(stage.getLeftTypeCheck(), left, stage.getSymbol(),stage.getTypeErrorFormat());
                typeCheck(stage.getRightTypeCheck(),right, stage.getSymbol(),stage.getTypeErrorFormat());
            } else {
                if (!stage.getTypeCheck().apply(left,right)){
                    throw new RuntimeException("&&&&&");
                }
            }
        }

        return stage.getOperator().apply(left,right,map);
    }

    /**目前没有用到此方法**/
    static ExpressionToken[] optimizeTokens(ExpressionToken[] tokens) {

        int symbol = 0;
        for (int i = 0 ; i < tokens.length; i++){
            if (tokens[i].getKind() != COMPARATOR) {
                continue;
            }
            symbol = comparatorMap.get(String.valueOf(tokens[i].getValue()));
            if (symbol != REQ && symbol != NEQ ) {
                continue;
            }
            i++;
            ExpressionToken token = tokens[i];
            if(token.getKind() == STRING) {
                token.setKind(PATTERN);
                token.setValue(Pattern.compile((String)token.getValue()));
                tokens[i] = token;
            }
        }
        return tokens;
    }

    public static Boolean typeCheck(Function<Object,Boolean> check,Object value, int symbol,String format){
        if (null == check ){
            return false;
        }

        return check.apply(value);
    }

    public Object evaluate(Map<String, Object> map) {
        if(null == map){
            return this.eval(null);
        }
        return eval(map);
    }

    private Object eval(Map<String,Object> parameters) {
        if(this.evaluationStages == null)
            return null;
        if(null != parameters){
            parameters.put("orig", parameters);
            return evaluateStage(this.evaluationStages, parameters);
        }

        return evaluateStage(this.evaluationStages,parameters);
    }

}
