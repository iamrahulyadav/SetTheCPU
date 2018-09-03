package com.ansoft.speedup.profiles.condition;

import android.content.Context;
import android.os.Bundle;

import com.ansoft.speedup.R;
import com.ansoft.speedup.profiles.AppScanner;
import com.ansoft.speedup.profiles.conditionconfig.AppConfigFragment;

public class AppState extends Condition {
    public static final transient Class<?> config;
    public static final transient int format = 2131492913;
    public static final transient int name = 2131492897;
    private String activity;
    private String activityName;
    private transient boolean appIsLaunched;
    private boolean matchPackageOnly;

    static {
        config = AppConfigFragment.class;
    }

    public AppState() {
        this.appIsLaunched = false;
        this.matchPackageOnly = false;
        this.activityName = "";
        this.activity = "";
        this.type = "App";
        this.category = 1;
        this.scanners.add(AppScanner.class);
    }

    public String getName(Context context) {
        return context.getResources().getString(R.string.condition_app);
    }

    public String getFormattedName(Context context) {
        return String.format(context.getResources().getString(R.string.condition_format_app), new Object[]{this.activityName});
    }

    public boolean set(Bundle bundle) {
        if (!bundle.containsKey("activityName") || !bundle.containsKey("activity") || !bundle.containsKey("matchPackageOnly")) {
            return false;
        }
        this.activityName = bundle.getString("activityName");
        this.activity = bundle.getString("activity");
        this.matchPackageOnly = bundle.getBoolean("matchPackageOnly");
        return true;
    }

    public Bundle get() {
        Bundle bundle = new Bundle();
        bundle.putString("activityName", this.activityName);
        bundle.putString("activity", this.activity);
        bundle.putBoolean("matchPackageOnly", this.matchPackageOnly);
        return bundle;
    }

    public String getActivityName() {
        return this.activityName;
    }

    public String getActivity() {
        return this.activity;
    }

    public boolean getMatchPackageOnly() {
        return this.matchPackageOnly;
    }

    public boolean updateState(Bundle bundle) {
        if (this.appIsLaunched == bundle.getBoolean("appIsLaunched")) {
            return false;
        }
        this.appIsLaunched = bundle.getBoolean("appIsLaunched");
        return true;
    }

    public boolean check() {
        return this.appIsLaunched;
    }

    public Class<?> getConfigFragment() {
        return config;
    }
}
