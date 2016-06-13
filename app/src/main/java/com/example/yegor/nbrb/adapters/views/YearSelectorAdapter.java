package com.example.yegor.nbrb.adapters.views;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.yegor.nbrb.R;
import com.example.yegor.nbrb.utils.Utils;

import java.util.List;

public class YearSelectorAdapter extends ArrayAdapter<String> {

    private static final int NOT_SELECTED = -1;
    private int selectedPos = NOT_SELECTED;

    private List<String> models;

    public YearSelectorAdapter(Context context, List<String> models) {
        super(context, R.layout.item_year, models);
        this.models = models;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_year, parent, false);
        }

        TextView year = (TextView) convertView.findViewById(R.id.year);
        year.setText(models.get(position));

        if (position == selectedPos)
            convertView.setBackgroundColor(Utils.getColor(R.color.colorAccent));
        else
            convertView.setBackgroundColor(Utils.getColor(android.R.color.white));


        return convertView;
    }

    public String getText(int position) {
        return models.get(position);
    }

    public void setSelection(int position) {
        if (selectedPos == position) {
            selectedPos = NOT_SELECTED;
        } else {
            selectedPos = position;
        }
        notifyDataSetChanged();
    }

}
