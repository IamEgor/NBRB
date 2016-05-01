package com.example.yegor.nbrb.exceptions;

public class NoConnectionException extends Exception {

    @Override
    public String getMessage() {
        return "No Internet Connection Exception";
    }

}