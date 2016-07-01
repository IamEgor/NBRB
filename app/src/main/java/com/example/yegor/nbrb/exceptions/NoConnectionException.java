package com.example.yegor.nbrb.exceptions;

import com.example.yegor.nbrb.R;
import com.example.yegor.nbrb.utils.Utils;

public class NoConnectionException extends Exception {

    @Override
    public String getMessage() {
        return Utils.getString(R.string.exception_no_connection);
    }

}