package com.example.yegor.nbrb.adapters;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.yegor.nbrb.R;
import com.example.yegor.nbrb.models.DailyExRatesOnDateModel;

import java.util.List;

public class CurrentRatesAdapter extends RecyclerView.Adapter<CurrentRatesAdapter.ViewHolder> {

    List<DailyExRatesOnDateModel> models;

    public CurrentRatesAdapter(List<DailyExRatesOnDateModel> models) {
        this.models = models;
    }

    public void setModels(List<DailyExRatesOnDateModel> models) {
        this.models = models;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(parent);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.setAbbr(models.get(position).getAbbreviation());
        holder.setRate(models.get(position).getRate());
        //TODO * Scale
    }

    @Override
    public int getItemCount() {
        return models.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        public static final int LAYOUT_ID = R.layout.item_current_rates;

        private CardView cv;
        private TextView abbr, rate;

        public ViewHolder(ViewGroup parent) {
            super(LayoutInflater.from(parent.getContext()).inflate(LAYOUT_ID, parent, false));

            cv = (CardView) itemView.findViewById(R.id.cv);
            abbr = (TextView) itemView.findViewById(R.id.abbr);
            rate = (TextView) itemView.findViewById(R.id.rate);

        }

        public void setAbbr(String abbr) {
            this.abbr.setText(abbr);
        }

        public void setRate(float rate) {
            this.rate.setText(String.valueOf(rate));
        }

    }


}
