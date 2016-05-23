package com.example.yegor.nbrb.adapters;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.yegor.nbrb.R;
import com.example.yegor.nbrb.models.SpinnerModel;

import java.util.List;

public class SpinnerAdapter extends ArrayAdapter<SpinnerModel> {

    private Context context;
    private List<SpinnerModel> models;
    private LayoutInflater inflater;

    public SpinnerAdapter(Context context, List<SpinnerModel> models) {
        super(context, R.layout.item_dropdown_spinner, models);

        this.context = context;
        this.models = models;
    }

    public View getCustomView(int position, View convertView,
                              ViewGroup parent) {

        TextView name = (TextView) inflate(R.layout.item_dropdown_spinner, parent);
        name.setText(models.get(position).getName());

        return name;
    }

    @Override
    public View getDropDownView(int position, View convertView,
                                ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    public View inflate(@LayoutRes int id, ViewGroup parent) {
        if (inflater == null)
            inflater = LayoutInflater.from(context);

        return inflater.inflate(id, parent, false);
    }

    public int getPosition(String abbr) {

        for (int i = 0; i < models.size(); i++)
            if (models.get(i).getAbbr().equals(abbr))
                return i;

        throw new RuntimeException();
    }

}