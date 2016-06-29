package com.example.yegor.nbrb.exceptions;

import java.io.IOException;

public class UnknownException extends IOException {

    public static final String TAG = UnknownException.class.getName();

    private String message;

    public UnknownException(String detailMessage) {
        super(detailMessage);
        this.message = detailMessage;
    }

    @Override
    public String getMessage() {
        return message;
    }

}
