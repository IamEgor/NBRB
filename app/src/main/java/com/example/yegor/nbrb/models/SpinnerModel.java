package com.example.yegor.nbrb.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.example.yegor.nbrb.utils.DateUtils;

public class SpinnerModel implements Parcelable {

    public static final Creator<SpinnerModel> CREATOR = new Creator<SpinnerModel>() {
        @Override
        public SpinnerModel createFromParcel(Parcel source) {
            return new SpinnerModel(source);
        }

        @Override
        public SpinnerModel[] newArray(int size) {
            return new SpinnerModel[size];
        }
    };

    private String name;
    private String abbr;
    private int scale;
    private long dateEnd;

    public SpinnerModel(String abbr, String name, int scale, long dateEnd) {
        this.name = name;
        this.abbr = abbr;
        this.scale = scale;
        this.dateEnd = dateEnd;
    }

    protected SpinnerModel(Parcel in) {
        this.name = in.readString();
        this.abbr = in.readString();
        this.scale = in.readInt();
        this.dateEnd = in.readLong();
    }

    public String getName() {
        return name;
    }

    public String getAbbr() {
        return abbr;
    }

    public int getScale() {
        return scale;
    }

    public long getDateEnd() {
        return dateEnd;
    }

    public String getDateEndStr() {
        return dateEnd == -1 ? "" : DateUtils.format(dateEnd);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SpinnerModel model = (SpinnerModel) o;

        if (scale != model.scale) return false;
        if (dateEnd != model.dateEnd) return false;
        if (!name.equals(model.name)) return false;
        return abbr.equals(model.abbr);

    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + abbr.hashCode();
        result = 31 * result + scale;
        result = 31 * result + (int) (dateEnd ^ (dateEnd >>> 32));
        return result;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.abbr);
        dest.writeInt(this.scale);
        dest.writeLong(this.dateEnd);
    }

}
