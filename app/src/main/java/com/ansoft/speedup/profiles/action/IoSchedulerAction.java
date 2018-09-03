package com.ansoft.speedup.profiles.action;

import android.content.Context;
import android.os.Bundle;

import com.ansoft.speedup.R;
import com.ansoft.speedup.profiles.Profile;

public class IoSchedulerAction extends Action {
    public static final transient int format = 2131492954;
    public static final transient int name = 2131492950;
    private String scheduler;

    public IoSchedulerAction() {
        this.type = "IoScheduler";
    }

    public String getName(Context context) {
        return context.getResources().getString(R.string.action_set_io_scheduler);
    }

    public String getFormattedName(Context context) {
        return String.format(context.getResources().getString(R.string.action_format_set_io_scheduler), new Object[]{this.scheduler});
    }

    public boolean perform(ActionPerformer performer, Profile profile) {
        if (this.scheduler != null) {
            performer.getCpufreq().setIoScheduler(this.scheduler);
        }
        return true;
    }

    public boolean set(Bundle args) {
        if (!args.containsKey("scheduler")) {
            return false;
        }
        this.scheduler = args.getString("scheduler");
        return true;
    }

    public Bundle get() {
        Bundle bundle = new Bundle();
        bundle.putString("scheduler", this.scheduler);
        return bundle;
    }
}
