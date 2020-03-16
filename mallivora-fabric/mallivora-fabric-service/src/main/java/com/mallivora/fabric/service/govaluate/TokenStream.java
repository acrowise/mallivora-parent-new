package com.mallivora.fabric.service.govaluate;

public class TokenStream {

    private ExpressionToken[] tokens;
    private int index;
    private int tokenLength;

    public ExpressionToken[] getTokens() {
        return tokens;
    }

    public void setTokens(ExpressionToken[] tokens) {
        this.tokens = tokens;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getTokenLength() {
        return tokenLength;
    }

    public void setTokenLength(int tokenLength) {
        this.tokenLength = tokenLength;
    }
    public TokenStream(ExpressionToken[] tokens) {
        this.tokens = tokens;
        this.tokenLength = tokens.length;
    }

    public ExpressionToken next(){
        ExpressionToken token = this.tokens[this.index];
        this.index += 1;
        return token;
    }

    public boolean hashNext(){
        return this.index < this.tokenLength;
    }

    public void rewind(){
        this.index -= 1;
    }
}
