package com.example.yegor.nbrb.models;

public class SpinnerModel {

    private String abbr;
    private String name;
    private String dateEnd;

    public SpinnerModel(String abbr, String name, String dateEnd) {
        this.abbr = abbr;
        this.name = name;

        if (dateEnd == null || dateEnd.length() == 0)
            this.dateEnd = "";
        else
            this.dateEnd = dateEnd.substring(0, 10);

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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SpinnerModel that = (SpinnerModel) o;

        if (!abbr.equals(that.abbr)) return false;
        if (!name.equals(that.name)) return false;
        return dateEnd != null ? dateEnd.equals(that.dateEnd) : that.dateEnd == null;

    }

    @Override
    public int hashCode() {
        int result = abbr.hashCode();
        result = 31 * result + name.hashCode();
        result = 31 * result + (dateEnd != null ? dateEnd.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "SpinnerModel{" +
                "name='" + name + '\'' +
                ", abbr='" + abbr + '\'' +
                ", dateEnd='" + dateEnd + '\'' +
                '}';
    }
}
