package com.zackehh.dotnotes.util;

/**
 * A wrapping class for a potential key - keys
 * can be either Strings or Integers, depending
 * on whether we're working with an Array or an
 * Object. This class simply allows for safer
 * usage under the circumstances.
 */
public class NotedKey {

    /**
     * The internal key backing this NotedKey.
     */
    private final Object key;

    /**
     * String constructor, taking a String value and
     * setting the internal key against it.
     *
     * @param s the String value of this key
     */
    private NotedKey(String s){
        this.key = s;
    }

    /**
     * Integer constructor, taking an Integer value and
     * setting the internal key against it. This takes an
     * `int` because there should be no use case for wishing
     * to pass `null`.
     *
     * @param i the Integer value of this key
     */
    private NotedKey(Integer i){
        this.key = i;
    }

    /**
     * Returns the internal key as an Integer, assuming it is
     * a Integer. If it's not a Integer, you'll get a null value.
     *
     * @return the internal key as a Integer
     */
    public Integer asNumber(){
        return isNumber() ? (Integer) this.key : null;
    }

    /**
     * Returns the internal key as a String, assuming it is
     * a String. If it's not a String, you'll get a null value.
     *
     * @return the internal key as a String
     */
    public String asString(){
        return isString() ? (String) this.key : null;
    }

    /**
     * Determines whether the internal key is a Number or not.
     *
     * @return true if the key is a Number
     */
    public boolean isNumber(){
        return this.key instanceof Number;
    }

    /**
     * Determines whether the internal key is a String or not.
     *
     * @return true if the key is a String
     */
    public boolean isString(){
        return this.key instanceof String;
    }

    /**
     * Static creation method accepting an Object. If the Object
     * is a valid String, it will create a NotedKey wrapping a String.
     * Likewise if the Object is a Number, same applies with a Number value.
     *
     * If neither match, a null value is simply returned.
     *
     * @param o the Object to create for
     * @return a NotedKey wrapping a valid key
     */
    public static NotedKey of(Object o){
        if (o instanceof Number) {
            return new NotedKey((Integer) o);
        }
        if (o instanceof String) {
            return new NotedKey((String) o);
        }
        return null;
    }

    /** {@inheritDoc} **/
    @Override
    public String toString(){
        return this.key.toString();
    }

}