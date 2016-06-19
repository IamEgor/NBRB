package com.example.yegor.nbrb.adapters;

import android.support.annotation.LayoutRes;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.yegor.nbrb.R;
import com.example.yegor.nbrb.models.SpinnerModel;
import com.example.yegor.nbrb.utils.DateUtils;
import com.example.yegor.nbrb.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class ChooseCurrencyAdapter extends RecyclerView.Adapter<ChooseCurrencyAdapter.ViewHolder> {

    private OnItemClickListener listener;
    private List<SpinnerModel> models;

    public ChooseCurrencyAdapter(List<SpinnerModel> models, OnItemClickListener listener) {
        this.models = new ArrayList<>(models);
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(parent);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bind(models.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return models.size();
    }

    public void animateTo(List<SpinnerModel> models) {
        applyAndAnimateRemovals(models);
        applyAndAnimateAdditions(models);
        applyAndAnimateMovedItems(models);
    }

    private void applyAndAnimateRemovals(List<SpinnerModel> newModels) {
        for (int i = models.size() - 1; i >= 0; i--) {
            final SpinnerModel model = models.get(i);
            if (!newModels.contains(model)) {
                removeItem(i);
            }
        }
    }

    private void applyAndAnimateAdditions(List<SpinnerModel> newModels) {
        for (int i = 0, count = newModels.size(); i < count; i++) {
            final SpinnerModel model = newModels.get(i);
            if (!models.contains(model)) {
                addItem(i, model);
            }
        }
    }

    private void applyAndAnimateMovedItems(List<SpinnerModel> newModels) {
        for (int toPosition = newModels.size() - 1; toPosition >= 0; toPosition--) {
            final SpinnerModel model = newModels.get(toPosition);
            final int fromPosition = models.indexOf(model);
            if (fromPosition >= 0 && fromPosition != toPosition) {
                moveItem(fromPosition, toPosition);
            }
        }
    }

    public SpinnerModel removeItem(int position) {
        final SpinnerModel model = models.remove(position);
        notifyItemRemoved(position);
        return model;
    }

    public void addItem(int position, SpinnerModel model) {
        models.add(position, model);
        notifyItemInserted(position);
    }

    public void moveItem(int fromPosition, int toPosition) {
        final SpinnerModel model = models.remove(fromPosition);
        models.add(toPosition, model);
        notifyItemMoved(fromPosition, toPosition);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @LayoutRes
        private static final int LAYOUT = R.layout.item_currency;

        private TextView abbr;
        private TextView name;
        private TextView dateEnd;

        public ViewHolder(ViewGroup parent) {
            super(LayoutInflater.from(parent.getContext()).inflate(LAYOUT, parent, false));

            abbr = (TextView) itemView.findViewById(R.id.abbr);
            name = (TextView) itemView.findViewById(R.id.name);
            dateEnd = (TextView) itemView.findViewById(R.id.date_end);
        }

        public void bind(SpinnerModel model, OnItemClickListener listener) {

            abbr.setText(model.getAbbr());
            name.setText(model.getName());

            if (model.getDateEnd() != -1)
                dateEnd.setText(String.format(Utils.getString(R.string.date_end),
                        DateUtils.format(model.getDateEnd())));
            else
                dateEnd.setText("");

            itemView.setOnClickListener(v -> listener.onItemClick(model));
        }

    }

    public interface OnItemClickListener {
        void onItemClick(SpinnerModel model);
    }

}
