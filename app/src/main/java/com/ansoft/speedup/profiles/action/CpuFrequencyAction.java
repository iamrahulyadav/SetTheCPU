package com.ansoft.speedup.profiles.action;

import android.content.Context;
import android.os.Bundle;

import com.ansoft.speedup.R;
import com.ansoft.speedup.profiles.Profile;
import org.joda.time.DateTimeConstants;

public class CpuFrequencyAction extends Action {
    public static final transient int format = 2131492952;
    public static final transient int name = 2131492948;
    private int maxKHz;
    private int minKHz;

    public CpuFrequencyAction() {
        this.maxKHz = 0;
        this.minKHz = 0;
        this.type = "CpuFrequency";
    }

    public String getName(Context context) {
        return context.getResources().getString(R.string.action_set_cpu);
    }

    public String getFormattedName(Context context) {
        return String.format(context.getResources().getString(R.string.action_format_set_cpu), new Object[]{Integer.valueOf(this.maxKHz / DateTimeConstants.MILLIS_PER_SECOND), Integer.valueOf(this.minKHz / DateTimeConstants.MILLIS_PER_SECOND)});
    }

    public boolean perform(ActionPerformer performer, Profile profile) {
        performer.getCpufreq().setFrequency(this.maxKHz, this.minKHz);
        return true;
    }

    public boolean set(Bundle args) {
        if (!args.containsKey("maxKHz") && !args.containsKey("minKHz")) {
            return false;
        }
        this.maxKHz = args.getInt("maxKHz");
        this.minKHz = args.getInt("minKHz");
        return true;
    }

    public Bundle get() {
        Bundle bundle = new Bundle();
        bundle.putInt("maxKHz", this.maxKHz);
        bundle.putInt("minKHz", this.minKHz);
        return bundle;
    }
}
