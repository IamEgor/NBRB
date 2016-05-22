package com.example.yegor.nbrb.views;

import android.support.annotation.IntDef;
import android.widget.EditText;

import com.example.yegor.nbrb.R;
import com.example.yegor.nbrb.utils.Utils;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class Validator {

    private static final SimpleDateFormat format2 =
            new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

    public static final int VALID = 0;
    public static final int INVALID_FORMAT = 1;
    public static final int TOO_EARLY_YET = 2;
    public static final int TOO_OLD_DATE = 3;

    private EditText editText;

    public Validator(EditText editText) {
        this.editText = editText;
    }

    public static String getMessage(@Result int result) {

        switch (result) {
            case INVALID_FORMAT:
                return String.format("Should match input pattern [%s]",
                        Utils.getString(R.string.input_date_pattern));
            case TOO_EARLY_YET:
                return Utils.getString(R.string.input_date_too_early);
            case TOO_OLD_DATE:
                return Utils.getString(R.string.input_date_too_old);
            default:
                return null;

        }

    }

    @Result
    public int getResult() {
        try {

            String s = editText.getText().toString();

            if (!s.matches(Utils.getString(R.string.input_date_pattern_numbers)))
                return INVALID_FORMAT;

            long date2long = Utils.date2long(s);

            if (date2long > Utils.getEdgeTime()) {
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
