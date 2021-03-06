package com.example.yegor.nbrb.views;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.example.yegor.nbrb.App;
import com.example.yegor.nbrb.R;
import com.example.yegor.nbrb.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class ToggleNavigation extends LinearLayout implements View.OnClickListener {

    private static int ACTIVE_COLOR = App.getContext().getResources().getColor(R.color.colorPrimary);
    private static int IN_ACTIVE_COLOR = App.getContext().getResources().getColor(R.color.light_grey);

    private Context context;
    private OnChoose onChoose;

    private List<ButtonParam> params;
    private List<Integer> ids;

    private int activeId;
    private int previousSelectedId;
    private int canRepeatId;

    public ToggleNavigation(Context context, List<ButtonParam> labels) {
        super(context);
        this.context = context;
        this.params = labels;
        init(context, labels);
    }

    public ToggleNavigation(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    public ToggleNavigation(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ToggleNavigation(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.context = context;
    }

    public int getActiveId() {
        return activeId;
    }

    public int getPreviousSelectedId() {
        return previousSelectedId;
    }

    public void setParams(List<ButtonParam> params) {
        this.params = params;
        init(context, params);
    }

    public void setOnChoose(OnChoose onChoose) {
        this.onChoose = onChoose;
    }

    public void setActive(int activeId) {

        previousSelectedId = this.activeId;
        this.setActiveStateless(activeId);
    }

    public void setActiveStateless(int activeId) {
        this.activeId = activeId;

        for (ButtonParam param : params) {
            int id = param.getId();
            param.setActive(id == activeId);
            findViewById(id).setBackgroundColor(id == activeId ? ACTIVE_COLOR : IN_ACTIVE_COLOR);
        }
    }

    public void setPreviousActive() {
        setActiveStateless(previousSelectedId);
    }

    @Override
    public void onClick(View v) {

        if (activeId == v.getId() && canRepeatId != v.getId())
            return;

        setActive(v.getId());

        if (onChoose == null)
            throw new UnsupportedOperationException("You must implement callback operation");

        onChoose.choose(getPositionById(v.getId()));
    }

    private int getPositionById(int id) {
        for (int i = 0; i < params.size(); i++) {
            if (params.get(i).getId() == id)
                return i;
        }
        throw new RuntimeException("Smth got wrong");
    }

    private void init(Context context, List<ButtonParam> labels) {

        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        p.weight = 1;

        ids = new ArrayList<>();

        for (ButtonParam param : labels) {

            Button button = new Button(context);

            button.setLayoutParams(p);
            button.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);

            button.setText(param.getLabel());
            button.setId(param.getId());
            button.setBackgroundColor(param.isActive() ? ACTIVE_COLOR : IN_ACTIVE_COLOR);

            button.setOnClickListener(this);

            this.addView(button);

            if (param.isActive())
                activeId = param.getId();

            if (param.isCanRepeat())
                canRepeatId = param.getId();

        }
    }

    public static class ButtonParam {

        private String label;
        private boolean active;
        private boolean canRepeat;
        private int id;

        public ButtonParam(String label, boolean active, boolean canRepeat) {
            this.label = label;
            this.active = active;
            this.canRepeat = canRepeat;
            id = Utils.generateViewId();
        }

        public String getLabel() {
            return label;
        }

        public boolean isActive() {
            return active;
        }

        public boolean isCanRepeat() {
            return canRepeat;
        }

        public int getId() {
            return id;
        }

        public void setActive(boolean active) {
            this.active = active;
        }
    }

    public interface OnChoose {
        void choose(int position);
    }

}
