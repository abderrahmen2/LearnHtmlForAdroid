package com.utils;

/**
 * Created by Novice on 2017/5/1.
 */

public class ResultSimple {

    Boolean theBoolean;
    String message;

    public ResultSimple(Boolean theBoolean, String message) {
        this.theBoolean = theBoolean;
        this.message = message;
    }

    public ResultSimple(Boolean theBoolean) {
        this.theBoolean = theBoolean;
    }

    public Boolean isBoolean() {
        return theBoolean;
    }


    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
