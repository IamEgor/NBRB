package com.example.yegor.nbrb.models;

import org.ksoap2.serialization.SoapObject;

public class ExRatesDynModel {

    public static final String DATE = "Date";
    public static final String RATE = "Cur_OfficialRate";

    private String date;
    private float rate;

    public ExRatesDynModel(String date, float rate) {
        this.date = date;
        this.rate = rate;
    }

    public ExRatesDynModel(SoapObject soapObject) {
        date = soapObject.getProperty(DATE).toString().substring(0, 10);
        rate = Float.parseFloat(soapObject.getProperty(RATE).toString());
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public float getRate() {
        return rate;
    }

    public void setRate(float rate) {
        this.rate = rate;
    }

    @Override
    public String toString() {
        return "ExRatesDynModel{" +
                "date='" + date + '\'' +
                ", rate=" + rate +
                '}';
    }


    static class Builder {

        private String date;
        private float rate;

        public Builder setDate(String date) {
            this.date = date;
            return this;
        }

        public Builder setRate(float rate) {
            this.rate = rate;
            return this;
        }

        public ExRatesDynModel create() {
            return new ExRatesDynModel(date, rate);
        }

    }
}
