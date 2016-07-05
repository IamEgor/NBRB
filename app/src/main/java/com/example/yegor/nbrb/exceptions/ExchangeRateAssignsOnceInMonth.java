package com.example.yegor.nbrb.exceptions;

import com.example.yegor.nbrb.R;
import com.example.yegor.nbrb.utils.Utils;

import java.io.IOException;

public class ExchangeRateAssignsOnceInMonth extends IOException {

    @Override
    public String getMessage() {
        return Utils.getString(R.string.exception_exchange_rate_assigns_once_in_month);
    }
    
}
