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

    private List<DailyExRatesOnDateModel> models;

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

        DailyExRatesOnDateModel model = models.get(position);

        holder.setScale(String.valueOf(model.getScale()));
        holder.setAbbr(model.getAbbreviation());
        holder.setRate(model.getRate());
        holder.setName(model.getQuotName());
    }

    @Override
    public int getItemCount() {
        return models.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        public static final int LAYOUT_ID = R.layout.item_current_rates;

        private CardView cv;
        private TextView scale, abbr, rate, name;

        public ViewHolder(ViewGroup parent) {
            super(LayoutInflater.from(parent.getContext()).inflate(LAYOUT_ID, parent, false));

            cv = (CardView) itemView.findViewById(R.id.cv);
            scale = (TextView) itemView.findViewById(R.id.scale);
            abbr = (TextView) itemView.findViewById(R.id.abbr);
            rate = (TextView) itemView.findViewById(R.id.rate);
            name = (TextView) itemView.findViewById(R.id.name);

        }

        public void setScale(String scale) {
            this.scale.setText(scale);
        }

        public void setAbbr(String abbr) {
            this.abbr.setText(abbr);
        }

        public void setRate(float rate) {
            this.rate.setText(String.valueOf(rate));
        }

        public void setName(String name) {
            this.name.setText(name);
        }

    }


}
