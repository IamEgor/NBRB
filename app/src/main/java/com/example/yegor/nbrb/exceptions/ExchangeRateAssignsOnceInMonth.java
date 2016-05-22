package com.example.yegor.nbrb.exceptions;

import java.io.IOException;

public class ExchangeRateAssignsOnceInMonth extends IOException {

    @Override
    public String getMessage() {
        return "Yhe exchange rate is assigned once a month";
    }

}
