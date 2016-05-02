package com.example.yegor.nbrb.exceptions;

import java.io.IOException;

public class NoDataFoundException extends IOException {

    @Override
    public String getMessage() {
        return "No Data Found Exception ";
    }

}
