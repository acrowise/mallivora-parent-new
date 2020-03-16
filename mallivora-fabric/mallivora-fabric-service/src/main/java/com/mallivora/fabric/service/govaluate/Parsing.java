package com.mallivora.fabric.service.govaluate;

import org.apache.commons.lang3.ArrayUtils;

import com.mallivora.fabric.service.utils.CharacterUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Function;

public class Parsing {

    public static ExpressionToken[] parseTokens(String expression, Map functions){
        ExpressionToken[] tokens = new ExpressionToken[]{};
        LexerStream lexerStream = LexerStream.newLexerStream(expression);
        LexerState state = LexerState.validLexerStates[0];
        while(lexerStream.canRead()){
            ExpressionToken token = readToken(lexerStream,state,functions);
            state = state.getLexerStateForToken(token.getKind());
            tokens = ArrayUtils.add(tokens, token);
        }
        return tokens;
    }

    public static ExpressionToken readToken(LexerStream stream, LexerState state, Map<String, Function> functions){
        Object function;
        Object tokenValue = null;
        ExpressionToken ret = new ExpressionToken();
        String tokenString;
        int kind = 0;
        boolean found;
        boolean completed;
        while(stream.canRead()){
            int character = stream.readCharacter();
            if(Character.isSpaceChar(character)){
                continue;
            }
            kind = TokenKind.UNKNOWN;

            if(CharacterUtils.isNumeric(character)){
                tokenString = readTokenUntilFalse(stream,CharacterUtils::isNumeric);
                if(".".equals(tokenString)){
                    int a = '.';
                    tokenValue = Float.valueOf(String.valueOf(a));
                }else {
                    tokenValue = Float.parseFloat(tokenString);
                }
                kind = TokenKind.NUMERIC;
                break;
            }
            // 44 == ','
            if(character == ','){
                tokenValue = ',';
                kind = TokenKind.SEPARATOR;
                break;
            }
            // 91 == '['
            if(character == '['){
                tokenValue = readUntilFalse(stream,true,false,true,CharacterUtils::isNotClosingBracket);
                kind = TokenKind.VARIABLE;
                stream.rewind(-1);
                break;
            }

            if(Character.isLetter(character)){
                tokenString = readTokenUntilFalse(stream, CharacterUtils::isVariableName);
                tokenValue = tokenString;
                kind = TokenKind.VARIABLE;
                if("true".equals(tokenValue)){
                   kind = TokenKind.BOOLEAN;
                   tokenValue = true;
                } else {
                  if("false".equals(tokenValue)){
                      kind = TokenKind.BOOLEAN;
                      tokenValue = false;
                  }
                }
                // textual operator?
                if("in".equals(tokenValue) || "IN".equals(tokenValue)){
                    tokenValue = "in";
                    kind = TokenKind.COMPARATOR;
                }
                function = functions.get(tokenString);
                if(null != function){
                    kind = TokenKind.FUNCTION;
                    tokenValue = function;
                }
                break;
            }

            if(!CharacterUtils.isNotQuote(character)){
                tokenValue = readUntilFalse(stream, true, false, true, CharacterUtils::isNotQuote);

                stream.rewind(-1);
                try {
                    tryParseTime(String.valueOf(tokenValue));
                    kind = TokenKind.TIME;
                    tokenValue = new Date();
                }catch (Exception e){
                    kind = TokenKind.STRING;
                }
                break;
            }

            if(character == '('){
                tokenValue = character;
                kind = TokenKind.CLAUSE;
                break;
            }

            if(character == ')'){
                tokenValue = character;
                kind = TokenKind.CLAUSE_CLOSE;
                break;
            }

            tokenString = readTokenUntilFalse(stream, CharacterUtils::isNotAlphanumeric);
            tokenValue = tokenString;
            if(state.canTransitionTo(TokenKind.PREFIX)){
                if(OperatorSymbol.prefixSymbols(tokenString)){
                    kind = TokenKind.PREFIX;
                    break;
                }
            }

            if(OperatorSymbol.modifierSymbols(tokenString)){
                kind = TokenKind.MODIFIER;
                break;
            }

            if(OperatorSymbol.logicalSymbols(tokenString)){
                kind = TokenKind.LOGICALOP;
                break;
            }

            if(OperatorSymbol.comparatorSymbols(tokenString)){
                kind = TokenKind.COMPARATOR;
                break;
            }

            if(OperatorSymbol.ternarySymbols(tokenString)){
                kind = TokenKind.TERNARY;
                break;
            }
            throw new RuntimeException("");
        }
        ret.setKind(kind);
        ret.setValue(tokenValue);
        return ret;
    }

    public static String readTokenUntilFalse(LexerStream stream, Function<Integer, Boolean> function){
        stream.rewind(1);
        return readUntilFalse(stream,false,true,true,function);
    }

    public static String readUntilFalse(LexerStream stream, boolean includeWhitespace, boolean breakWhitespace, boolean allowEscaping, Function<Integer, Boolean> function){
        boolean conditioned = false;
        StringBuffer buffer = new StringBuffer();
        while(stream.canRead()){
            int character = stream.readCharacter();
            if(allowEscaping && character == '\\'){
                buffer.append((char) stream.readCharacter());
                continue;
            }

            if(Character.isSpaceChar(character)){
                if(breakWhitespace && buffer.length() > 0 ){
                    conditioned = true;
                    break;
                }
                if(!includeWhitespace){
                    continue;
                }
            }
            if(function.apply(character)){
                buffer.append((char)character);
            }else{
                conditioned = true;
                stream.rewind(1);
                break;
            }
        }
        return buffer.toString();
    }

    public static void tryParseTime(String time) throws Exception {
        throw new Exception();
    }

    public LexerState getLexerStateForToken(int kind){
        return null;
    }




}
