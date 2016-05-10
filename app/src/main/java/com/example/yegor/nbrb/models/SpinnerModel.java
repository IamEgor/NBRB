package com.example.yegor.nbrb.models;

import com.example.yegor.nbrb.App;
import com.example.yegor.nbrb.R;

public class SpinnerModel {

    private String name;
    private String abbr;
    private String dateEnd;

    public SpinnerModel(String abbr, String name, String dateEnd) {
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

    public String getDateEnd() {
        return dateEnd;
    }

    @Override
    public String toString() {

        if (dateEnd == null || dateEnd.isEmpty())
            return String.format(App.getContext().getString(R.string.searchable_spinner_item_text1), abbr, name);
        else
            return String.format(App.getContext().getString(R.string.searchable_spinner_item_text2), abbr, name, getDateEnd());

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SpinnerModel that = (SpinnerModel) o;

        if (!name.equals(that.name)) return false;
        if (!abbr.equals(that.abbr)) return false;
        return dateEnd != null ? dateEnd.equals(that.dateEnd) : that.dateEnd == null;

    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + abbr.hashCode();
        result = 31 * result + (dateEnd != null ? dateEnd.hashCode() : 0);
        return result;
    }

}
