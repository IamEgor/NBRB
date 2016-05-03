package com.example.yegor.nbrb.adapters;

import android.content.Context;
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
    private LayoutInflater inflater;
    private List<SpinnerModel> models;

    public SpinnerAdapter(Context context, List<SpinnerModel> models) {
        super(context, R.layout.item_dropdown_spinner, models);

        this.context = context;
        this.models = models;
    }

    public View getCustomView(int position, View convertView,
                              ViewGroup parent) {

        LayoutInflater inflater = getLayoutInflater();

        View layout = inflater.inflate(R.layout.item_dropdown_spinner, parent, false);

        SpinnerModel model = models.get(position);

        TextView name = (TextView) layout.findViewById(R.id.abbr);
        TextView abbr = (TextView) layout.findViewById(R.id.name);
        TextView date_end = (TextView) layout.findViewById(R.id.date_end);

        name.setText(model.getName());
        abbr.setText(model.getAbbr());
        if(!model.getDateEnd().isEmpty())
        date_end.setText(String.format(context.getString(R.string.until_date), model.getDateEnd()));

        return layout;
    }

    // It gets a View that displays in the drop down popup the data at the specified position
    @Override
    public View getDropDownView(int position, View convertView,
                                ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    // It gets a View that displays the data at the specified position
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    public LayoutInflater getLayoutInflater() {
        if (inflater == null)
            inflater = LayoutInflater.from(context);
        return inflater;
    }
}