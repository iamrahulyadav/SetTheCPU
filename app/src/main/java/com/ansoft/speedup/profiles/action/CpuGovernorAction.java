package com.ansoft.speedup.profiles.action;

import android.content.Context;
import android.os.Bundle;

import com.ansoft.speedup.R;
import com.ansoft.speedup.profiles.Profile;

public class CpuGovernorAction extends Action {
    public static final transient int format = 2131492953;
    public static final transient int name = 2131492949;
    private transient boolean applyParams;
    private String governor;
    private transient boolean init;

    public CpuGovernorAction() {
        this.init = false;
        this.applyParams = false;
        this.type = "CpuGovernor";
    }

    public String getName(Context context) {
        return context.getResources().getString(R.string.action_set_governor);
    }

    public String getFormattedName(Context context) {
        return String.format(context.getResources().getString(R.string.action_format_set_governor), new Object[]{this.governor});
    }

    public boolean perform(ActionPerformer performer, Profile profile) {
        if (!this.init) {
            this.init = true;
            this.applyParams = performer.getContext().getSharedPreferences("SpeedUp", 0).getBoolean("governorSetOnProfile", false);
            this.init = true;
        }
        if (this.governor != null) {
            performer.getCpufreq().setGovernor(this.governor);
        }
        if (this.applyParams) {
            performer.getCpufreq().setGovernorFromSaved(performer.getCpufreq().getGovernor(), performer.getContext().getFilesDir().getAbsolutePath());
        }
        return true;
    }

    public boolean set(Bundle args) {
        if (!args.containsKey("governor")) {
            return false;
        }
        this.governor = args.getString("governor");
        return true;
    }

    public Bundle get() {
        Bundle bundle = new Bundle();
        bundle.putString("governor", this.governor);
        return bundle;
    }
}
