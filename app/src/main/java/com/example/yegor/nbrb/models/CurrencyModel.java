package com.example.yegor.nbrb.models;

import com.example.yegor.nbrb.utils.Utils;

import org.ksoap2.serialization.SoapObject;

import java.text.ParseException;

public class CurrencyModel {

    public static final String ID = "Cur_Id";
    public static final String QUOT_NAME = "Cur_QuotName";
    public static final String QUOT_NAME_ENG = "Cur_QuotName_Eng";
    public static final String SCALE = "Cur_Scale";
    public static final String CODE = "Cur_Code";
    public static final String ABBR = "Cur_Abbreviation";
    public static final String NAME = "Cur_Name";
    public static final String NAME_ENG = "Cur_Name_Eng";
    public static final String DATE_START = "Cur_DateStart";
    public static final String DATE_END = "Cur_DateEnd";
    public static final String PARENT_ID = "Cur_ParentID";

    private int id;
    private String quotName;
    private String quotNameEng;
    private int scale;
    private String code;
    private String abbr;
    private String name;
    private String nameEng;
    private long dateStart;
    private long dateEnd;
    private int parentId;

    public CurrencyModel(int id, String quotName, String quotNameEng, int scale, String code,
                         String abbr, String name, String nameEng, long dateStart, long dateEnd,
                         int parentId) {
        this.id = id;
        this.quotName = quotName;
        this.quotNameEng = quotNameEng;
        this.scale = scale;
        this.code = code;
        this.abbr = abbr;
        this.name = name;
        this.nameEng = nameEng;
        this.dateStart = dateStart;
        this.dateEnd = dateEnd;
        this.parentId = parentId;
    }

    public CurrencyModel(SoapObject soapObject) {
        id = Integer.parseInt(soapObject.getProperty(ID).toString());
        quotName = soapObject.getProperty(QUOT_NAME).toString();
        quotNameEng = soapObject.getProperty(QUOT_NAME_ENG).toString();
        scale = Integer.parseInt(soapObject.getProperty(SCALE).toString());
        code = soapObject.getProperty(CODE).toString();
        abbr = soapObject.getProperty(ABBR).toString();
        name = soapObject.getProperty(NAME).toString();
        nameEng = soapObject.getProperty(NAME_ENG).toString();
        dateStart = prop2long(soapObject.getProperty(DATE_START).toString());
        dateEnd = prop2long(soapObject.getPropertySafelyAsString(DATE_END, null));
        parentId = Integer.parseInt(soapObject.getProperty(PARENT_ID).toString());
    }

    public int getId() {
        return id;
    }

    public String getIdStr() {
        return String.valueOf(id);
    }

    public String getQuotName() {
        return quotName;
    }

    public String getQuotNameEng() {
        return quotNameEng;
    }

    public int getScale() {
        return scale;
    }

    public String getCode() {
        return code;
    }

    public String getAbbr() {
        return abbr;
    }

    public String getName() {
        return name;
    }

    public String getNameEng() {
        return nameEng;
    }

    public long getDateStart() {
        return dateStart;
    }

    public long getDateEnd() {
        return dateEnd;
    }

    public int getParentId() {
        return parentId;
    }

    private long prop2long(String s) {

        if (s.length() == 0)
            return -1;

        try {
            return Utils.date2long(s.substring(0, 10));
        } catch (ParseException e) {
            Utils.logT(CurrencyModel.class.getSimpleName(), s + " " + e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public String toString() {
        return "Currency{" +
                "id=" + id +
                ", quotName='" + quotName + '\'' +
                ", quotNameEng='" + quotNameEng + '\'' +
                ", scale='" + scale + '\'' +
                ", code='" + code + '\'' +
                ", abbr='" + abbr + '\'' +
                ", name='" + name + '\'' +
                ", nameEng='" + nameEng + '\'' +
                ", dateStart='" + dateStart + '\'' +
                ", dateEnd='" + dateEnd + '\'' +
                ", parentId='" + parentId + '\'' +
                '}';
    }

    public static class Builder {

        private int id;
        private String quotName;
        private String quotNameEng;
        private int scale;
        private String code;
        private String abbr;
        private String name;
        private String nameEng;
        private long dateStart;
        private long dateEnd;
        private int parentId;

        public Builder setId(int id) {
            this.id = id;
            return this;
        }

        public Builder setQuotName(String quotName) {
            this.quotName = quotName;
            return this;
        }

        public Builder setQuotNameEng(String quotNameEng) {
            this.quotNameEng = quotNameEng;
            return this;
        }

        public Builder setScale(int scale) {
            this.scale = scale;
            return this;
        }

        public Builder setCode(String code) {
            this.code = code;
            return this;
        }

        public Builder setAbbr(String abbr) {
            this.abbr = abbr;
            return this;
        }

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder setNameEng(String nameEng) {
            this.nameEng = nameEng;
            return this;
        }

        public Builder setDateStart(long dateStart) {
            this.dateStart = dateStart;
            return this;
        }

        public Builder setDateEnd(long dateEnd) {
            this.dateEnd = dateEnd;
            return this;
        }

        public Builder setParentId(int parentId) {
            this.parentId = parentId;
            return this;
        }

        public CurrencyModel create() {
            return new CurrencyModel(id, quotName, quotNameEng, scale, code, abbr, name, nameEng,
                    dateStart, dateEnd, parentId);
        }

    }

}
