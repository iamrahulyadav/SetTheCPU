package com.ansoft.speedup.profiles.condition;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.ansoft.speedup.R;
import com.ansoft.speedup.profiles.conditionconfig.ScreenStateConfigFragment;

public class ScreenState extends Condition {
    public static final transient Class<?> config;
    public static final transient int format = 2131492905;
    public static final transient int name = 2131492888;
    private boolean on;
    private transient boolean screenIsOn;

    static {
        config = ScreenStateConfigFragment.class;
    }

    public ScreenState() {
        this.screenIsOn = true;
        this.on = false;
        this.type = "Screen";
        this.category = 1;
        this.intents.add("android.intent.action.SCREEN_ON");
        this.intents.add("android.intent.action.SCREEN_OFF");
    }

    public String getName(Context context) {
        return context.getResources().getString(R.string.condition_screen);
    }

    public String getFormattedName(Context context) {
        if (this.on) {
            return String.format(context.getResources().getString(R.string.condition_format_screen), new Object[]{context.getResources().getString(R.string.condition_on)});
        }
        return String.format(context.getResources().getString(R.string.condition_format_screen), new Object[]{context.getResources().getString(R.string.condition_off)});
    }

    public Class<?> getConfigFragment() {
        return config;
    }

    public boolean set(Bundle args) {
        if (!args.containsKey("on")) {
            return false;
        }
        this.on = args.getBoolean("on");
        return true;
    }

    public Bundle get() {
        Bundle bundle = new Bundle();
        bundle.putBoolean("on", this.on);
        return bundle;
    }

    public boolean updateState(Intent intent) {
        boolean newOn = true;
        if (intent.getAction().equals("android.intent.action.SCREEN_ON")) {
            newOn = true;
        } else if (intent.getAction().equals("android.intent.action.SCREEN_OFF")) {
            newOn = false;
        }
        this.screenIsOn = newOn;
        return true;
    }

    public boolean check() {
        return this.on == this.screenIsOn;
    }
}
