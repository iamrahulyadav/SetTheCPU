package com.ansoft.speedup.profiles.condition;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.ansoft.speedup.R;
import com.ansoft.speedup.profiles.conditionconfig.BatteryTempConfigFragment;

public class BatteryTempState extends Condition {
    public static final transient Class<?> config;
    public static final transient int format = 2131492907;
    public static final transient int name = 2131492891;
    private String inequality;
    private transient boolean matches;
    private int temp;

    static {
        config = BatteryTempConfigFragment.class;
    }

    public BatteryTempState() {
        this.matches = false;
        this.temp = 500;
        this.inequality = ">=";
        this.type = "BatteryTemp";
        this.intents.add("android.intent.action.BATTERY_CHANGED");
        this.category = 1;
    }

    public String getName(Context context) {
        return context.getResources().getString(R.string.condition_battery_temp);
    }

    public String getFormattedName(Context context) {
        return String.format(context.getResources().getString(R.string.condition_format_battery_temp), new Object[]{this.inequality, Integer.valueOf(Math.round((float) (this.temp / 10))), Integer.valueOf((int) Math.round(((((double) this.temp) / 10.0d) * 1.8d) + 32.0d))});
    }

    public boolean updateState(Intent intent) {
        boolean nowMatches = this.matches;
        int nowTemp = intent.getIntExtra("temperature", 0);
        this.matches = false;
        if ("<".equals(this.inequality)) {
            if (nowTemp < this.temp) {
                this.matches = true;
            }
        } else if (">".equals(this.inequality)) {
            if (nowTemp > this.temp) {
                this.matches = true;
            }
        } else if ("<=".equals(this.inequality)) {
            if (nowTemp <= this.temp) {
                this.matches = true;
            }
        } else if (">=".equals(this.inequality)) {
            if (nowTemp >= this.temp) {
                this.matches = true;
            }
        } else if ("==".equals(this.inequality)) {
            if (nowTemp == this.temp) {
                this.matches = true;
            }
        } else if ("!=".equals(this.inequality) && nowTemp != this.temp) {
            this.matches = true;
        }
        if (nowMatches != this.matches) {
            return true;
        }
        return false;
    }

    public boolean set(Bundle bundle) {
        if (!bundle.containsKey("temp") || !bundle.containsKey("inequality")) {
            return false;
        }
        this.temp = bundle.getInt("temp");
        this.inequality = bundle.getString("inequality");
        return true;
    }

    public Bundle get() {
        Bundle bundle = new Bundle();
        bundle.putInt("temp", this.temp);
        bundle.putString("inequality", this.inequality);
        return bundle;
    }

    public boolean check() {
        return this.matches;
    }

    public Class<?> getConfigFragment() {
        return config;
    }
}
