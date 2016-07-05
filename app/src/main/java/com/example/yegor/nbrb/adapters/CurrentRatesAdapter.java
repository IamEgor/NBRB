package com.example.yegor.nbrb.adapters;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.example.yegor.nbrb.App;
import com.example.yegor.nbrb.R;
import com.example.yegor.nbrb.fragments.RatesGraphicFragment;
import com.example.yegor.nbrb.models.CurrencyModel;
import com.example.yegor.nbrb.models.ExRatesOnDateModel;

import java.util.List;

public class CurrentRatesAdapter extends RecyclerView.Adapter<CurrentRatesAdapter.ViewHolder> {

    private List<ExRatesOnDateModel> models;
    private LinearLayoutManager manager;

    private int lastPosition = -1;
    private int cur_delay;
    private boolean firstBind = true;

    public CurrentRatesAdapter(List<ExRatesOnDateModel> models, LinearLayoutManager manager) {
        this.models = models;
        this.manager = manager;
    }

    public void setModels(List<ExRatesOnDateModel> models) {
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
        setAnimation(holder.cv, position);
    }

    @Override
    public void onViewDetachedFromWindow(ViewHolder holder) {
        super.onViewDetachedFromWindow(holder);

        holder.clearAnimation();
    }

    @Override
    public int getItemCount() {
        return models.size();
    }

    private void setAnimation(View viewToAnimate, int position) {

        if (position > lastPosition) {

            Animation animation = AnimationUtils.loadAnimation(App.getContext(), R.anim.slide_in_up);

            if (firstBind) {

                if (manager.findFirstVisibleItemPosition() ==
                        manager.findFirstCompletelyVisibleItemPosition()) {

                    animation.setStartOffset(cur_delay);
                    int ANIMATION_DELAY = 100;
                    cur_delay += ANIMATION_DELAY;
                } else
                    firstBind = false;
            }

            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }

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

        public void bind(ExRatesOnDateModel model) {

            scale.setText(String.valueOf(model.getScale()));
            abbr.setText(model.getAbbreviation());
            rate.setText(String.valueOf(model.getRate()));
            name.setText(model.getQuotName());
        }

        public void clearAnimation() {
            itemView.clearAnimation();
        }

    }

}
