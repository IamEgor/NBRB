package com.example.yegor.nbrb.models;

import org.ksoap2.serialization.SoapObject;

public class ExRatesOnDateModel {

    public static final String NAME = "Cur_Name";
    public static final String SCALE = "Cur_Scale";
    public static final String RATE = "Cur_OfficialRate";
    public static final String CODE = "Cur_Code";
    public static final String ABBREV = "Cur_Abbreviation";

    private String quotName;
    private int scale;
    private float rate;
    private int code;
    private String abbreviation;

    public ExRatesOnDateModel(String quotName, int scale, float rate, int code, String abbreviation) {
        this.quotName = quotName;
        this.scale = scale;
        this.rate = rate;
        this.code = code;
        this.abbreviation = abbreviation;
    }

    public ExRatesOnDateModel(SoapObject soapObject) {
        quotName = soapObject.getProperty(NAME).toString();
        scale = Integer.parseInt(soapObject.getProperty(SCALE).toString());
        rate = Float.parseFloat(soapObject.getProperty(RATE).toString());
        code = Integer.parseInt(soapObject.getProperty(CODE).toString());
        abbreviation = soapObject.getProperty(ABBREV).toString();
    }

    public String getQuotName() {
        return quotName;
    }

    public void setQuotName(String quotName) {
        this.quotName = quotName;
    }

    public int getScale() {
        return scale;
    }

    public void setScale(int scale) {
        this.scale = scale;
    }

    public float getRate() {
        return rate;
    }

    public void setRate(float rate) {
        this.rate = rate;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getAbbreviation() {
        return abbreviation;
    }

    public void setAbbreviation(String abbreviation) {
        this.abbreviation = abbreviation;
    }

    @Override
    public String toString() {
        return "ExRatesOnDateModel{" +
                "quotName='" + quotName + '\'' +
                ", scale=" + scale +
                ", rate=" + rate +
                ", code=" + code +
                ", abbreviation='" + abbreviation + '\'' +
                '}';
    }

    static class Builder {

        private String quotName;
        private int scale;
        private float rate;
        private int code;
        private String abbreviation;

        public Builder setQuotName(String quotName) {
            this.quotName = quotName;
            return this;
        }

        public Builder setScale(int scale) {
            this.scale = scale;
            return this;
        }

        public Builder setRate(float rate) {
            this.rate = rate;
            return this;
        }

        public Builder setCode(int code) {
            this.code = code;
            return this;
        }

        public Builder setAbbreviation(String abbreviation) {
            this.abbreviation = abbreviation;
            return this;
        }

        public ExRatesOnDateModel create() {
            return new ExRatesOnDateModel(quotName, scale, rate, code, abbreviation);
        }

    }

}