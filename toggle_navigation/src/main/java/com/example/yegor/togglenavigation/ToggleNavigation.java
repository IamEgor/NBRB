package com.example.yegor.togglenavigation;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Build;
import android.support.annotation.ColorRes;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

public class ToggleNavigation extends LinearLayout implements View.OnClickListener {
    @ColorRes
    private static int ACTIVE_COLOR = R.color.colorPrimary;
    @ColorRes
    private static int IN_ACTIVE_COLOR = R.color.white;

    private Context context;
    private Resources resources;
    private OnChoose onChoose;

    private List<ButtonParam> params;
    private List<Integer> ids;

    private int activeId;
    private int previousSelectedId;
    //Invoke callback even on active view
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
        resources = context.getResources();
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

    public int getActivePosition() {
        return getPositionById(activeId);
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

    private void setActiveId(int activeId) {

        previousSelectedId = this.activeId;
        setActiveIdStateless(activeId);
    }

    public void setActiveIdStateless(final int activeId) {
        this.activeId = activeId;

        for (ButtonParam param : params) {

            int id = param.getId();
            param.setActive(id == activeId);
            Button button = (Button) findViewById(id);
            button.setBackgroundColor(id == activeId ?
                    resources.getColor(ACTIVE_COLOR) : resources.getColor(IN_ACTIVE_COLOR));
            button.setTextColor(id == activeId ?
                    resources.getColor(IN_ACTIVE_COLOR) : resources.getColor(ACTIVE_COLOR));
        }
    }

    public void setActivePosition(final int activePosition) {

        if (activePosition < 0 || activePosition > params.size() - 1)
            throw new RuntimeException("No such position.");

        ButtonParam buttonParam;
        activeId = 0;

        for (int i = 0; i < params.size(); i++) {

            buttonParam = params.get(i);

            int id = buttonParam.getId();
            Button button = (Button) findViewById(id);

            if (i == activePosition)
                activeId = id;

            buttonParam.setActive(id == activeId);
            button.setBackgroundColor(id == activeId ?
                    resources.getColor(ACTIVE_COLOR) : resources.getColor(IN_ACTIVE_COLOR));
            button.setTextColor(id == activeId ?
                    resources.getColor(IN_ACTIVE_COLOR) : resources.getColor(ACTIVE_COLOR));
        }
    }

    public void setPreviousActive() {
        setActiveIdStateless(previousSelectedId);
    }

    private int getPositionById(int id) {

        for (int i = 0; i < params.size(); i++)
            if (params.get(i).getId() == id)
                return i;

        throw new RuntimeException("No such position.");
    }

    private void init(Context context, List<ButtonParam> labels) {

        LinearLayout.LayoutParams p = new LinearLayout
                .LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);

        p.weight = 1;

        setBackgroundResource(R.drawable.border_drawable);
        Utils.setPaddingMoreThan(this, 3);

        ids = new ArrayList<>();

        for (ButtonParam param : labels) {

            Button button = new Button(context);

            button.setLayoutParams(p);
            button.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);

            button.setText(param.getLabel());
            button.setId(param.getId());
            button.setBackgroundColor(param.isActive() ?
                    resources.getColor(ACTIVE_COLOR) : resources.getColor(IN_ACTIVE_COLOR));
            button.setTextColor(param.isActive() ?
                    resources.getColor(IN_ACTIVE_COLOR) : resources.getColor(ACTIVE_COLOR));
            button.setTypeface(null, Typeface.BOLD);

            button.setOnClickListener(this);

            this.addView(button);

            if (param.isActive())
                activeId = param.getId();

            if (param.isCanRepeat())
                canRepeatId = param.getId();
        }
    }

    @Override
    public void onClick(View v) {

        if (activeId == v.getId() && canRepeatId != v.getId())
            return;

        setActiveId(v.getId());

        if (onChoose == null)
            throw new UnsupportedOperationException("You must implement callback operation");

        onChoose.onToggleChoose(getPositionById(v.getId()));
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
        void onToggleChoose(int position);
    }

}