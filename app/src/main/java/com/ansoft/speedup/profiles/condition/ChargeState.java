package com.ansoft.speedup.profiles.condition;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.ansoft.speedup.R;
import com.ansoft.speedup.profiles.conditionconfig.ChargeConfigFragment;
import org.joda.time.MutableDateTime;
import org.joda.time.chrono.EthiopicChronology;

public class ChargeState extends Condition {
    public static final transient Class<?> config;
    public static final transient int format = 2131492904;
    public static final transient int name = 2131492887;
    private transient boolean matches;
    private String mode;

    static {
        config = ChargeConfigFragment.class;
    }

    public ChargeState() {
        this.matches = false;
        this.mode = "Any";
        this.type = "Charge";
        this.intents.add("android.intent.action.BATTERY_CHANGED");
        this.category = 1;
    }

    public String getName(Context context) {
        return context.getResources().getString(R.string.condition_charge);
    }

    public String getFormattedName(Context context) {
        return String.format(context.getResources().getString(R.string.condition_format_charge), new Object[]{this.mode});
    }

    public boolean updateState(Intent intent) {
        boolean nowMatches = this.matches;
        int nowType = intent.getIntExtra("plugged", 0);
        this.matches = false;
        switch (nowType) {
            case EthiopicChronology.EE /*1*/:
                if ("Any".equals(this.mode) || "AC".equals(this.mode)) {
                    this.matches = true;
                    break;
                }
            case MutableDateTime.ROUND_CEILING /*2*/:
                if ("Any".equals(this.mode) || "USB".equals(this.mode)) {
                    this.matches = true;
                    break;
                }
            case MutableDateTime.ROUND_HALF_CEILING /*4*/:
                if ("Any".equals(this.mode) || "wireless".equals(this.mode)) {
                    this.matches = true;
                    break;
                }
        }
        if (nowMatches != this.matches) {
            return true;
        }
        return false;
    }

    public boolean set(Bundle bundle) {
        if (!bundle.containsKey("mode")) {
            return false;
        }
        this.mode = bundle.getString("mode");
        return true;
    }

    public Bundle get() {
        Bundle bundle = new Bundle();
        bundle.putString("mode", this.mode);
        return bundle;
    }

    public boolean check() {
        return this.matches;
    }

    public Class<?> getConfigFragment() {
        return config;
    }
}
