package com.mallivora.fabric.service.govaluate;

public class LexerStream {

    private int[] source;
    private int position;
    private int length;

    public int[] getSource() {
        return source;
    }

    public void setSource(int[] source) {
        this.source = source;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public static LexerStream newLexerStream(String source){
        LexerStream ret = new LexerStream();
        int[] runes = new int[source.toCharArray().length];
        for(int i = 0; i < source.toCharArray().length; i++){
            runes[i] = source.toCharArray()[i];
        }
        ret.setSource(runes);
        ret.setLength(runes.length);
        return ret;
    }

    public boolean canRead(){
        return this.position < this.length;
    }

    public void rewind(int amount){ this.position -= amount; }

    public int readCharacter(){
        int character = this.source[this.position];
        this.position += 1;
        return character;
    }

}
