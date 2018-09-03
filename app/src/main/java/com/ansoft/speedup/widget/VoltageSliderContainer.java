package com.ansoft.speedup.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import com.ansoft.speedup.R;
import com.ansoft.speedup.widget.TextSlider.OnSnapListener;

public class VoltageSliderContainer extends LinearLayout {
    private int delta;
    private OnVoltageChangeListener mOnVoltageChangeListener;
    private int max;
    private int min;
    private TextSlider sliderVoltage;
    private String[] voltageDisplay;

    public interface OnVoltageChangeListener {
        void onVoltageChange(int i);
    }

    /* renamed from: com.ansoft.setthecpu.widget.VoltageSliderContainer.1 */
    class C02061 implements OnSnapListener {
        C02061() {
        }

        public void onSnap(String freq) {
            VoltageSliderContainer.this.mOnVoltageChangeListener.onVoltageChange(VoltageSliderContainer.this.getVoltage());
        }
    }

    public VoltageSliderContainer(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.sliderVoltage = null;
        setOrientation(VERTICAL);
    }

    public void init(Integer voltage, int max, int min, int delta) {
        if (max >= min && max - (min % delta) == 0) {
            this.max = max;
            this.min = min;
            this.delta = delta;
            this.voltageDisplay = new String[(((max - min) / delta) + 1)];
            int i = min;
            int j = 0;
            while (i <= max) {
                this.voltageDisplay[j] = Integer.toString(i);
                i += delta;
                j++;
            }
            if (voltage != null) {
                initChildren(voltage.intValue());
            } else {
                initChildren(min);
            }
        }
    }

    public void setVoltage(Integer voltage) {
        if (voltage.intValue() < this.min) {
            voltage = Integer.valueOf(this.min);
        } else if (voltage.intValue() > this.max) {
            voltage = Integer.valueOf(this.max);
        } else if (voltage.intValue() % this.delta != 0) {
            if (voltage.intValue() % this.delta > this.delta / 2) {
                voltage = Integer.valueOf(voltage.intValue() + (this.delta - (voltage.intValue() % this.delta)));
            } else {
                voltage = Integer.valueOf(voltage.intValue() - (voltage.intValue() % this.delta));
            }
        }
        this.sliderVoltage.setValue(voltage.toString());
    }

    public int getVoltage() {
        return this.min + (this.sliderVoltage.getIndex().intValue() * this.delta);
    }

    public void setOnFreqChangeListener(OnVoltageChangeListener l) {
        this.mOnVoltageChangeListener = l;
    }

    private void initChildren(int voltage) {
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View v = getChildAt(i);
            if ((v instanceof TextSlider) && this.sliderVoltage == null) {
                this.sliderVoltage = (TextSlider) v;
                this.sliderVoltage.init(this.voltageDisplay, Integer.valueOf(0), 0);
                this.sliderVoltage.setCaption(getResources().getString(R.string.mV));
                setVoltage(Integer.valueOf(voltage));
                this.sliderVoltage.setOnSnapListener(new C02061());
            }
        }
    }
}
