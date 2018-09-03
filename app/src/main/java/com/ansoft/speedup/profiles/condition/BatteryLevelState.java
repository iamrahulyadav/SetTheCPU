package com.ansoft.speedup.profiles.condition;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.ansoft.speedup.R;
import com.ansoft.speedup.profiles.conditionconfig.BatteryLevelConfigFragment;

public class BatteryLevelState extends Condition {
    public static final transient Class<?> config;
    public static final transient int format = 2131492906;
    public static final transient int name = 2131492890;
    private String inequality;
    private int level;
    private transient boolean matches;

    static {
        config = BatteryLevelConfigFragment.class;
    }

    public BatteryLevelState() {
        this.matches = false;
        this.level = 50;
        this.inequality = "<=";
        this.type = "BatteryLevel";
        this.intents.add("android.intent.action.BATTERY_CHANGED");
        this.category = 1;
    }

    public String getName(Context context) {
        return context.getResources().getString(R.string.condition_battery);
    }

    public String getFormattedName(Context context) {
        return String.format(context.getResources().getString(R.string.condition_format_battery), new Object[]{this.inequality, Integer.valueOf(this.level)}) + "%";
    }

    public boolean updateState(Intent intent) {
        boolean nowMatches = this.matches;
        int nowLevel = intent.getIntExtra("level", 0);
        this.matches = false;
        if ("<".equals(this.inequality)) {
            if (nowLevel < this.level) {
                this.matches = true;
            }
        } else if (">".equals(this.inequality)) {
            if (nowLevel > this.level) {
                this.matches = true;
            }
        } else if ("<=".equals(this.inequality)) {
            if (nowLevel <= this.level) {
                this.matches = true;
            }
        } else if (">=".equals(this.inequality)) {
            if (nowLevel >= this.level) {
                this.matches = true;
            }
        } else if ("==".equals(this.inequality)) {
            if (nowLevel == this.level) {
                this.matches = true;
            }
        } else if ("!=".equals(this.inequality) && nowLevel != this.level) {
            this.matches = true;
        }
        if (nowMatches != this.matches) {
            return true;
        }
        return false;
    }

    public boolean set(Bundle bundle) {
        if (!bundle.containsKey("level") || !bundle.containsKey("inequality")) {
            return false;
        }
        this.level = bundle.getInt("level");
        this.inequality = bundle.getString("inequality");
        return true;
    }

    public Bundle get() {
        Bundle bundle = new Bundle();
        bundle.putInt("level", this.level);
        bundle.putString("inequality", this.inequality);
        return bundle;
    }

    public boolean check() {
        return this.matches;
    }

    public Class<?> getConfigFragment() {
        return BatteryLevelConfigFragment.class;
    }
}
