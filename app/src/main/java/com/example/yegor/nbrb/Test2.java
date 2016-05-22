package com.example.yegor.nbrb;

import android.support.annotation.IntDef;

import com.example.yegor.nbrb.utils.Utils;
import com.example.yegor.nbrb.views.Validator;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.text.ParseException;

public class Test2 {

    public static final int VALID = 0;
    public static final int INVALID_FORMAT = 1;
    public static final int TOO_EARLY_YET = 2;
    public static final int TOO_OLD_DATE = 3;

    public static String string;

    public static void main(String[] args) {

        string = "2016-02-2l";

        int result = getResult();

        switch (result) {
            case Validator.VALID:
                print("valid");
                break;
            case Validator.INVALID_FORMAT:
            case Validator.TOO_EARLY_YET:
            case Validator.TOO_OLD_DATE:
                print(getMessage(result));
        }

    }

    private static void print(String s) {
        System.out.println(s);
    }


    public static String getMessage(@Result int result) {

        switch (result) {
            case INVALID_FORMAT:
                return String.format("Should match input pattern [%s]", "yyyy-MM-dd");
            case TOO_EARLY_YET:
                return "Exchange rate hasn't been installed";
            case TOO_OLD_DATE:
                return "Too old date";
            default:
                return null;

        }

    }

    @Result
    public static int getResult() {
        try {

            if (!string.matches("\\d{4}-\\d{2}-\\d{2}"))
                return INVALID_FORMAT;

            long date2long = Utils.date2long(string);
            print(Utils.format(date2long));
            if (date2long > Utils.getEdgeTime()) {
                //TODO здесь проблема
                Utils.log(Utils.format(date2long) + " " + Utils.format(Utils.getEdgeTime()));
                return TOO_EARLY_YET;
            } else if (date2long < Utils.START_DATE)
                return TOO_OLD_DATE;
            else
                return VALID;
        } catch (ParseException e) {
            return INVALID_FORMAT;
        }
    }

    @IntDef({VALID, INVALID_FORMAT, TOO_EARLY_YET, TOO_OLD_DATE})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Result {
    }

}
