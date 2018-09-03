package com.ansoft.speedup.widget;

import android.content.Context;
import android.widget.GridView;
import com.ansoft.speedup.util.Cpufreq;

public class CoreView extends GridView {
    Cpufreq mCpufreq;
    int mNumCores;

    public CoreView(Context context) {
        super(context);
        this.mNumCores = this.mCpufreq.getNumCores();
    }
}
