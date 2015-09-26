package com.zackehh.dotnotes.util;

public class ParseException extends Exception {

    public ParseException(char c, int index, boolean brace){
        this("Unable to parse character '" + c + "' at column " + (index + 1) + "!" +
                (brace ? " Did you remember to wrap brace keys in quotes?" : ""));
    }

    public ParseException(String message) {
        super(message);
    }

}