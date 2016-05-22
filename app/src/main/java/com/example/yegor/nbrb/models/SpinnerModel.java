package com.example.yegor.nbrb.models;

import com.example.yegor.nbrb.App;
import com.example.yegor.nbrb.R;
import com.example.yegor.nbrb.utils.Utils;

public class SpinnerModel {

    private String name;
    private String abbr;
    private long dateEnd;

    public SpinnerModel(String abbr, String name, long dateEnd) {
        this.name = name;
        this.abbr = abbr;
        this.dateEnd = dateEnd;
    }

    public String getName() {
        return name;
    }

    public String getAbbr() {
        return abbr;
    }

    public long getDateEnd() {
        return dateEnd;
    }

    @Override
    public String toString() {

        if (dateEnd == -1)
            return String.format(App.getContext().getString(R.string.searchable_spinner_item_text1), abbr, name);
        else
            return String.format(App.getContext().getString(R.string.searchable_spinner_item_text2), abbr, name, Utils.format(dateEnd));

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SpinnerModel that = (SpinnerModel) o;

        if (dateEnd != that.dateEnd) return false;
        if (!name.equals(that.name)) return false;
        return abbr.equals(that.abbr);

    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + abbr.hashCode();
        result = 31 * result + (int) (dateEnd ^ (dateEnd >>> 32));
        return result;
    }

}
