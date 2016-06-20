package com.example.yegor.nbrb.adapters;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.yegor.nbrb.App;
import com.example.yegor.nbrb.R;
import com.example.yegor.nbrb.fragments.RatesGraphicFragment;
import com.example.yegor.nbrb.models.CurrencyModel;
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
        holder.bind(models.get(position));
    }

    @Override
    public int getItemCount() {
        return models.size();
    }


    static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

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

            cv.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

            Intent intent = new Intent(RatesGraphicFragment.ACTION);
            intent.putExtra(CurrencyModel.ABBR, abbr.getText().toString());
            intent.putExtra(CurrencyModel.SCALE, scale.getText().toString());

            LocalBroadcastManager.getInstance(App.getContext()).sendBroadcast(intent);
        }

        public void bind(DailyExRatesOnDateModel model) {

            scale.setText(String.valueOf(model.getScale()));
            abbr.setText(model.getAbbreviation());
            rate.setText(String.valueOf(model.getRate()));
            name.setText(model.getQuotName());
        }
    }
}
