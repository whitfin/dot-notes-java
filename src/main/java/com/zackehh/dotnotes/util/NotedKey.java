package com.zackehh.dotnotes.util;

public class NotedKey {

    private Object o;

    public NotedKey(String s){
        this.o = s;
    }

    public NotedKey(int i){
        this.o = i;
    }

    public String asString(){
        return isString() ? (String) o : null;
    }

    public Integer asNumber(){
        return isNumber() ? (Integer) o : null;
    }

    public boolean isNumber(){
        return o instanceof Number;
    }

    public boolean isString(){
        return o instanceof String;
    }

    public static NotedKey of(Object o){
        if (o instanceof Number) {
            return new NotedKey((Integer) o);
        }
        return new NotedKey((String) o);
    }

    @Override
    public String toString(){
        return o.toString();
    }

}