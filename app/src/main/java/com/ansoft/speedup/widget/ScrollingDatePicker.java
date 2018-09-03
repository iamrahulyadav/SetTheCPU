package com.ansoft.speedup.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewParent;
import android.widget.DatePicker;

public class ScrollingDatePicker extends DatePicker {
    public ScrollingDatePicker(Context context) {
        super(context);
    }

    public ScrollingDatePicker(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ScrollingDatePicker(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (ev.getAction() == 0) {
            ViewParent p = getParent();
            if (p != null) {
                p.requestDisallowInterceptTouchEvent(true);
            }
        }
        return false;
    }
}
