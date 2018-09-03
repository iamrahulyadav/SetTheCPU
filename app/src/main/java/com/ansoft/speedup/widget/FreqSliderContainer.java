package com.ansoft.speedup.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

import com.ansoft.speedup.R;
import com.ansoft.speedup.widget.TextSlider.OnSnapListener;
import java.util.HashMap;
import java.util.Map;
import org.joda.time.DateTimeConstants;

public class FreqSliderContainer extends LinearLayout {
    private boolean enabled;
    private String[] freqDisplay;
    private Integer[] freqKHz;
    private Map<Integer, String> freqMap;
    private OnFreqChangeListener mOnFreqChangeListener;
    private int parentHeight;
    private int parentWidth;
    private TextSlider sliderMax;
    private TextSlider sliderMin;

    public interface OnFreqChangeListener {
        void onFreqChange(int i, int i2);
    }

    /* renamed from: com.ansoft.setthecpu.widget.FreqSliderContainer.1 */
    class C02021 implements OnSnapListener {
        C02021() {
        }

        public void onSnap(String freq) {
            FreqSliderContainer.this.arrangeScrollers(FreqSliderContainer.this.sliderMax);
            if (FreqSliderContainer.this.mOnFreqChangeListener != null) {
                FreqSliderContainer.this.mOnFreqChangeListener.onFreqChange(FreqSliderContainer.this.getMax(), FreqSliderContainer.this.getMin());
            }
        }
    }

    /* renamed from: com.ansoft.setthecpu.widget.FreqSliderContainer.2 */
    class C02032 implements OnSnapListener {
        C02032() {
        }

        public void onSnap(String freq) {
            FreqSliderContainer.this.arrangeScrollers(FreqSliderContainer.this.sliderMin);
            if (FreqSliderContainer.this.mOnFreqChangeListener != null) {
                FreqSliderContainer.this.mOnFreqChangeListener.onFreqChange(FreqSliderContainer.this.getMax(), FreqSliderContainer.this.getMin());
            }
        }
    }

    public FreqSliderContainer(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.sliderMax = null;
        this.sliderMin = null;
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

    public void init(Integer[] freqs, int max, int min) {
        this.freqKHz = freqs;
        this.freqMap = new HashMap();
        this.freqDisplay = new String[this.freqKHz.length];
        for (int i = 0; i < this.freqKHz.length; i++) {
            this.freqDisplay[i] = Integer.valueOf(this.freqKHz[i].intValue() / DateTimeConstants.MILLIS_PER_SECOND).toString();
            this.freqMap.put(this.freqKHz[i], this.freqDisplay[i]);
        }
        initChildren(max, min);
    }

    private void initChildren(int max, int min) {
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View v = getChildAt(i);
            if (v instanceof TextSlider) {
                if (this.sliderMax == null) {
                    this.sliderMax = (TextSlider) v;
                    this.sliderMax.init(this.freqDisplay, Integer.valueOf(0), this.parentWidth);
                    this.sliderMax.setCaption(getResources().getString(R.string.max_slider));
                    setMax(Integer.valueOf(max));
                    this.sliderMax.setOnSnapListener(new C02021());
                } else if (this.sliderMin == null) {
                    this.sliderMin = (TextSlider) v;
                    this.sliderMin.init(this.freqDisplay, Integer.valueOf(0), this.parentWidth);
                    this.sliderMin.setCaption(getResources().getString(R.string.min_slider));
                    setMin(Integer.valueOf(min));
                    this.sliderMin.setOnSnapListener(new C02032());
                    return;
                }
            }
        }
    }

    public void setMax(Integer max) {
        if (this.freqMap.containsKey(max)) {
            this.sliderMax.setValue((String) this.freqMap.get(max));
        }
    }

    public void setMin(Integer min) {
        if (this.freqMap.containsKey(min)) {
            this.sliderMin.setValue((String) this.freqMap.get(min));
        }
    }

    public int getMax() {
        return this.freqKHz[this.sliderMax.getIndex().intValue()].intValue();
    }

    public int getMin() {
        return this.freqKHz[this.sliderMin.getIndex().intValue()].intValue();
    }

    public void setOnFreqChangeListener(OnFreqChangeListener l) {
        this.mOnFreqChangeListener = l;
    }

    private void arrangeScrollers(TextSlider source) {
        if (this.sliderMax.getIndex().intValue() >= this.sliderMin.getIndex().intValue()) {
            return;
        }
        if (source == this.sliderMax) {
            this.sliderMin.setIndex2(this.sliderMax.getIndex().intValue());
        } else if (source == this.sliderMin) {
            this.sliderMax.setIndex2(this.sliderMin.getIndex().intValue());
        }
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        this.sliderMax.setEnabled(enabled);
        this.sliderMin.setEnabled(enabled);
    }

    public boolean onTouchEvent(MotionEvent ev) {
        if (ev.getAction() == 2) {
            requestDisallowInterceptTouchEvent(true);
        }
        return super.onTouchEvent(ev);
    }
}
