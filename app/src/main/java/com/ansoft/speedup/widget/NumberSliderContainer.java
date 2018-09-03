package com.ansoft.speedup.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import com.ansoft.speedup.widget.TextSlider.OnSnapListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class NumberSliderContainer extends LinearLayout {
    private boolean enabled;
    private String[] freqDisplay;
    private ArrayList<Integer> freqKHz;
    private Map<Integer, String> freqMap;
    private OnFreqChangeListener mOnFreqChangeListener;
    private int parentHeight;
    private int parentWidth;
    private TextSlider sliderMax;

    public interface OnFreqChangeListener {
        void onFreqChange(int i);
    }

    /* renamed from: com.ansoft.setthecpu.widget.NumberSliderContainer.1 */
    class C02041 implements OnSnapListener {
        C02041() {
        }

        public void onSnap(String freq) {
            NumberSliderContainer.this.arrangeScrollers(NumberSliderContainer.this.sliderMax);
            if (NumberSliderContainer.this.mOnFreqChangeListener != null) {
                NumberSliderContainer.this.mOnFreqChangeListener.onFreqChange(NumberSliderContainer.this.getMax());
            }
        }
    }

    public NumberSliderContainer(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.sliderMax = null;
        setOrientation(VERTICAL);
        setClickable(true);
        setFocusable(true);
        setFocusableInTouchMode(true);
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        this.parentWidth = MeasureSpec.getSize(widthMeasureSpec);
        this.parentHeight = MeasureSpec.getSize(heightMeasureSpec);
    }

    public void init(int max, int min, int defaultVal, String after, String caption) {
        int i;
        this.freqKHz = new ArrayList();
        for (i = min; i <= max; i++) {
            this.freqKHz.add(Integer.valueOf(i));
        }
        this.freqMap = new HashMap();
        this.freqDisplay = new String[this.freqKHz.size()];
        for (i = 0; i < this.freqKHz.size(); i++) {
            this.freqDisplay[i] = ((Integer) this.freqKHz.get(i)).toString() + after;
            this.freqMap.put(this.freqKHz.get(i), this.freqDisplay[i]);
        }
        initChildren(defaultVal, caption);
    }

    private void initChildren(int defaultVal, String caption) {
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View v = getChildAt(i);
            if ((v instanceof TextSlider) && this.sliderMax == null) {
                this.sliderMax = (TextSlider) v;
                this.sliderMax.init(this.freqDisplay, Integer.valueOf(0), this.parentWidth);
                this.sliderMax.setCaption(caption);
                setMax(Integer.valueOf(defaultVal));
                this.sliderMax.setOnSnapListener(new C02041());
            }
        }
    }

    public void setMax(Integer max) {
        if (this.freqMap.containsKey(max)) {
            this.sliderMax.setValue((String) this.freqMap.get(max));
        }
    }

    public void setValue(Integer value) {
        Log.d("SpeedUp", "setting " + ((String) this.freqMap.get(value)));
        if (this.freqMap.containsKey(value)) {
            this.sliderMax.setValue2((String) this.freqMap.get(value));
        }
    }

    public int getMax() {
        return ((Integer) this.freqKHz.get(this.sliderMax.getIndex().intValue())).intValue();
    }

    public void setOnFreqChangeListener(OnFreqChangeListener l) {
        this.mOnFreqChangeListener = l;
    }

    private void arrangeScrollers(TextSlider source) {
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        this.sliderMax.setEnabled(enabled);
    }

    public boolean onTouchEvent(MotionEvent ev) {
        if (ev.getAction() == 2) {
            requestDisallowInterceptTouchEvent(true);
        }
        return super.onTouchEvent(ev);
    }
}
