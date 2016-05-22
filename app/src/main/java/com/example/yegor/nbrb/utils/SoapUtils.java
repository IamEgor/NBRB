package com.example.yegor.nbrb.utils;


import android.support.annotation.NonNull;
import android.util.Log;

import com.example.yegor.nbrb.exceptions.ExchangeRateAssignsOnceInMonth;
import com.example.yegor.nbrb.exceptions.NoDataFoundException;
import com.example.yegor.nbrb.models.CurrencyModel;
import com.example.yegor.nbrb.models.DailyExRatesOnDateModel;
import com.example.yegor.nbrb.models.ExRatesDynModel;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class SoapUtils {

    private static final String TAG = SoapUtils.class.getName();

    private static final String NAMESPACE = "http://www.nbrb.by/";
    private static final String URL = "http://www.nbrb.by/Services/ExRates.asmx?wsdl";


    private static final String METHOD_NAME = "ExRatesDaily2";
    private static final String SOAP_ACTION = "http://www.nbrb.by/ExRatesDaily2";
    private static final String PROPERTY = "onDate";


    private static final String METHOD_NAME2 = "ExRatesDyn";
    private static final String SOAP_ACTION2 = "http://www.nbrb.by/ExRatesDyn";
    private static final String PROPERTY_CUR_ID = "curId";
    private static final String PROPERTY_FROM_DATE = "fromDate";
    private static final String PROPERTY_TO_DATE = "toDate";


    private static final String METHOD_NAME3 = "CurrenciesRef";
    private static final String SOAP_ACTION3 = "http://www.nbrb.by/CurrenciesRef";
    private static final String PROPERTY3 = "Periodicity";


    private static final String METHOD_NAME4 = "ExRatesMonthly2";
    private static final String SOAP_ACTION4 = "http://www.nbrb.by/ExRatesMonthly2";
    private static final String PROPERTY4 = "onDate";

    public static List<CurrencyModel> getCurrenciesList() throws IOException {

        List<CurrencyModel> list = new ArrayList<>(231);

        Map<String, String> map = new HashMap<>();
        map.put(PROPERTY3, String.valueOf(0));

        SoapObject response = getResponse(new RequestProps(METHOD_NAME3, SOAP_ACTION3, map));
        SoapObject dailyExRatesOnDate;

        for (int k = 0; k < response.getPropertyCount(); k++) {
            dailyExRatesOnDate = (SoapObject) response.getProperty(k);
            list.add(new CurrencyModel(dailyExRatesOnDate));
        }

        return list;

    }

    public static DailyExRatesOnDateModel getCurrencyDaily(@NonNull String currency, @NonNull String time)
            throws IOException {

        Map<String, String> map = new HashMap<>();
        map.put(PROPERTY, time);

        SoapObject response = getResponse(new RequestProps(METHOD_NAME, SOAP_ACTION, map));
        SoapObject dailyExRatesOnDate;

        for (int k = 0; k < response.getPropertyCount(); k++) {
            dailyExRatesOnDate = (SoapObject) response.getProperty(k);
            if (currency.equals(dailyExRatesOnDate.getProperty(DailyExRatesOnDateModel.ABBREV).toString()))
                return new DailyExRatesOnDateModel(dailyExRatesOnDate);
        }

        throw new ExchangeRateAssignsOnceInMonth();
    }

    public static DailyExRatesOnDateModel getCurrencyMonthly(@NonNull String currency, @NonNull String time)
            throws IOException {


        Map<String, String> map = new HashMap<>();
        map.put(PROPERTY4, time);

        SoapObject response = getResponse(new RequestProps(METHOD_NAME4, SOAP_ACTION4, map));
        SoapObject dailyExRatesOnDate;

        for (int k = 0; k < response.getPropertyCount(); k++) {
            dailyExRatesOnDate = (SoapObject) response.getProperty(k);
            if (currency.equals(dailyExRatesOnDate.getProperty(DailyExRatesOnDateModel.ABBREV).toString()))
                return new DailyExRatesOnDateModel(dailyExRatesOnDate);
        }

        return null;

    }

    public static List<DailyExRatesOnDateModel> getCurrenciesNow() throws IOException {

        List<DailyExRatesOnDateModel> list = new ArrayList<>(16);

        Map<String, String> map = new HashMap<>();
        map.put(PROPERTY, Utils.format((Calendar.getInstance().getTimeInMillis())));

        SoapObject response = getResponse(new RequestProps(METHOD_NAME, SOAP_ACTION, map));
        SoapObject dailyExRatesOnDate;

        for (int k = 0; k < response.getPropertyCount(); k++) {
            dailyExRatesOnDate = (SoapObject) response.getProperty(k);
            list.add(new DailyExRatesOnDateModel(dailyExRatesOnDate));
        }

        return list;

    }

    public static List<ExRatesDynModel> getRatesDyn(String curId, String fromDate, String toDate)
            throws IOException {

        List<ExRatesDynModel> list = new ArrayList<>(16);

        Map<String, String> map = new HashMap<>(3);

        map.put(PROPERTY_CUR_ID, curId);
        map.put(PROPERTY_FROM_DATE, fromDate);
        map.put(PROPERTY_TO_DATE, toDate);

        SoapObject response = getResponse(new RequestProps(METHOD_NAME2, SOAP_ACTION2, map));
        SoapObject dailyExRatesOnDate;

        for (int k = 0; k < response.getPropertyCount(); k++) {
            dailyExRatesOnDate = (SoapObject) response.getProperty(k);
            //TODO Utils.date2Long() вместо substring
            list.add(new ExRatesDynModel(dailyExRatesOnDate));
        }

        return list;
    }

    public static SoapObject getResponse(RequestProps props) throws IOException {//HttpResponseException, SoapFault

        SoapObject request = new SoapObject(NAMESPACE, props.getMethod());

        System.setProperty("http.keepAlive", "false");

        for (String key : props.getProperties().keySet()) {
            request.addProperty(key, props.getProperties().get(key));
        }

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.setOutputSoapObject(request);
        envelope.dotNet = true;//-

        HttpTransportSE httpTransport = new HttpTransportSE(URL);
        httpTransport.debug = true;

        try {

            httpTransport.call(props.getAction(), envelope);

            SoapObject response = (SoapObject) envelope.getResponse();

            System.out.println("[SoapUtils]  envelope.getResponse() - " + envelope.getResponse());
            Log.w("SoapUtils", "  envelope.getResponse() - " + envelope.getResponse());

            if (response == null)
                throw new NoDataFoundException();

            //if (!response.hasProperty("diffgram"))
            //  throw new NoDataFoundException();
            response = (SoapObject) response.getProperty(1);//anyType
            response = (SoapObject) response.getProperty(0);//newDataSet

            return response;

        } catch (XmlPullParserException e) {
            Log.e(TAG, "XmlPullParserException : " + e.getMessage());
            throw new RuntimeException();
        }

    }

    static class RequestProps {

        private String method;
        private String action;
        private Map<String, String> properties;

        public RequestProps(String method, String action, Map<String, String> properties) {
            this.method = method;
            this.action = action;
            this.properties = properties;
        }

        public String getMethod() {
            return method;
        }

        public String getAction() {
            return action;
        }

        public Map<String, String> getProperties() {
            return properties;
        }

        @Override
        public String toString() {
            return "RequestProps{" +
                    "method='" + method + '\'' +
                    ", action='" + action + '\'' +
                    ", properties=" + properties +
                    '}';
        }
    }

}
