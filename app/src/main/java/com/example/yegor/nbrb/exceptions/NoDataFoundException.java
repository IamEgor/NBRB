package com.example.yegor.nbrb.exceptions;

import com.example.yegor.nbrb.R;
import com.example.yegor.nbrb.utils.Utils;

import java.io.IOException;

public class NoDataFoundException extends IOException {

    @Override
    public String getMessage() {
        return Utils.getString(R.string.exception_no_data_found);
    }

}
