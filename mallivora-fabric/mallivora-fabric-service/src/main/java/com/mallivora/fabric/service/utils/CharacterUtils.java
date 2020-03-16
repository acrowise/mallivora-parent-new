package com.mallivora.fabric.service.utils;

public class CharacterUtils {

    public static boolean isNumeric(int character) {
        return Character.isDigit(character) || character == '.';
    }

    public static boolean isVariableName(int character) {
        return Character.isDigit(character) || Character.isLetter(character) || character == '_';
    }

    public static boolean isNotClosingBracket(int character) {
        return character != ']';
    }

    public static boolean isNotQuote(int character) {
        return character != '\'' && character != '"';
    }

    public static boolean isNotAlphanumeric(int character) {
        return !(Character.isDigit(character) ||
                Character.isLetter(character) ||
                character == '(' ||
                character == ')' ||
                !isNotQuote(character));
    }
}
