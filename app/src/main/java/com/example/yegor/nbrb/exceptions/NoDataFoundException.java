package com.example.yegor.nbrb.exceptions;

public class NoDataFoundException extends Exception {

    @Override
    public String getMessage() {
        return "No Data Found Exception ";
    }

}
