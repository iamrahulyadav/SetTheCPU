package com.ansoft.speedup.widget;

import android.content.Context;
import android.graphics.Typeface;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ansoft.speedup.R;

public class SliderItem extends LinearLayout {
    protected TextView bottomView;
    protected String caption;
    protected Integer index;
    protected boolean isCenter;
    protected String text;
    protected TextView topView;

    public SliderItem(Context context, boolean isCenterView, int topTextSize, int centerTextSize, int bottomTextSize, float lineHeight) {
        super(context);
        this.isCenter = false;
        setLayoutParams(new LayoutParams(-1, -2));
        setupView(context, isCenterView, topTextSize, centerTextSize, bottomTextSize, lineHeight);
    }

    protected void setupView(Context context, boolean isCenterView, int minTextSize, int maxTextSize, int bottomTextSize, float lineHeight) {
        setOrientation(VERTICAL);
        setGravity(16);
        this.topView = new TextView(context);
        this.topView.setGravity(81);
        this.topView.setTypeface(null, Typeface.BOLD);
        this.topView.setTextColor(getResources().getColor(R.color.semi_colorPrimary));
        this.topView.setTextSize(1, (float) minTextSize);
        this.bottomView = new TextView(context);
        this.bottomView.setGravity(49);
        this.bottomView.setTextSize(1, (float) bottomTextSize);
        this.topView.setLineSpacing(0.0f, lineHeight);
        if (isCenterView) {
            this.isCenter = true;
            this.topView.setTextSize(1, (float) maxTextSize);
            this.bottomView.setTextSize(1, 12.0f);
            this.bottomView.setTextColor(getResources().getColor(R.color.colorPrimary));
            this.topView.setTextColor(getResources().getColor(R.color.colorPrimary));
        }
        addView(this.topView);
        addView(this.bottomView);
    }

    public void setVals(Integer index, String text) {
        this.text = text;
        this.index = index;
        refreshText();
    }

    public void setCaption(String caption) {
        this.caption = caption;
        refreshText();
    }

    protected void refreshText() {
        if (this.text != null) {
            this.topView.setText(this.text);
        } else {
            this.topView.setText("");
        }
        if (this.isCenter) {
            this.bottomView.setText(this.caption);
        }
    }

    public String getText() {
        return this.text;
    }

    public int getIndex() {
        return this.index.intValue();
    }
}
