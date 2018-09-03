package com.ansoft.speedup.profiles.condition;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.ansoft.speedup.R;
import com.ansoft.speedup.profiles.conditionconfig.BatteryTempConfigFragment;
import com.ansoft.speedup.util.Utils;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import org.joda.time.DateTimeConstants;

public class CpuTempState extends Condition {
    public static final transient Class<?> config;
    public static final int format = 2131492908;
    public static final int name = 2131492892;
    private static transient ArrayList<String> sensors;
    private String inequality;
    private transient boolean matches;
    private transient int nowTemp;
    private String sensor;
    private int temp;
    private double unitFactor;

    static {
        config = BatteryTempConfigFragment.class;
        sensors = new ArrayList();
        sensors.add("/sys/class/hwmon/hwmon0/device/temp1_input");
        sensors.add("/sys/devices/system/cpu/cpu0/cpufreq/cpu_temp");
        sensors.add("/sys/devices/platform/omap/omap_temp_sensor.0/temperature");
        sensors.add("/sys/class/thermal/thermal_zone1/temp");
        sensors.add("/sys/class/i2c-adapter/i2c-4/4-004c/temperature");
        sensors.add("/sys/devices/system/cpu/cpu0/cpufreq/FakeShmoo_cpu_temp");
    }

    public CpuTempState() {
        this.matches = false;
        this.sensor = null;
        this.unitFactor = -1.0d;
        this.temp = 500;
        this.inequality = ">=";
        this.type = "CpuTemp";
        this.category = 1;
        this.intents.add("android.intent.action.BATTERY_CHANGED");
        int tempCheck = -1;
        if (this.sensor == null) {
            Iterator i$ = sensors.iterator();
            while (i$.hasNext()) {
                String f = (String) i$.next();
                this.sensor = f;
                tempCheck = getTemperature();
                if (tempCheck > Integer.MIN_VALUE) {
                    this.sensor = f;
                    break;
                }
            }
        }
        tempCheck = getTemperature();
        if (this.unitFactor <= 0.0d) {
            if (tempCheck > -100 && tempCheck < 100) {
                this.unitFactor = 10.0d;
            } else if ((tempCheck >= 100 && tempCheck < DateTimeConstants.MILLIS_PER_SECOND) || (tempCheck <= -100 && tempCheck > -1000)) {
                this.unitFactor = 1.0d;
            } else if ((tempCheck >= DateTimeConstants.MILLIS_PER_SECOND && tempCheck < 10000) || (tempCheck <= -1000 && tempCheck > -10000)) {
                this.unitFactor = 0.1d;
            } else if ((tempCheck >= 10000 && tempCheck < 100000) || (tempCheck <= -10000 && tempCheck > -100000)) {
                this.unitFactor = 0.01d;
            }
        }
        updateState(new Intent());
    }

    public String getName(Context context) {
        return context.getResources().getString(R.string.condition_cpu_temp);
    }

    public String getFormattedName(Context context) {
        return String.format(context.getResources().getString(R.string.condition_format_cpu_temp), new Object[]{this.inequality, Integer.valueOf(Math.round((float) (this.temp / 10))), Integer.valueOf((int) Math.round(((((double) this.temp) / 10.0d) * 1.8d) + 32.0d))});
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

    public boolean updateState(Intent args) {
        boolean nowMatches = this.matches;
        this.matches = false;
        this.nowTemp = (int) Math.round(((double) getTemperature()) * this.unitFactor);
        if ("<".equals(this.inequality)) {
            if (this.nowTemp < this.temp) {
                this.matches = true;
            }
        } else if (">".equals(this.inequality)) {
            if (this.nowTemp > this.temp) {
                this.matches = true;
            }
        } else if ("<=".equals(this.inequality)) {
            if (this.nowTemp <= this.temp) {
                this.matches = true;
            }
        } else if (">=".equals(this.inequality)) {
            if (this.nowTemp >= this.temp) {
                this.matches = true;
            }
        } else if ("==".equals(this.inequality)) {
            if (this.nowTemp == this.temp) {
                this.matches = true;
            }
        } else if ("!=".equals(this.inequality) && this.nowTemp != this.temp) {
            this.matches = true;
        }
        if (nowMatches != this.matches) {
            return true;
        }
        return false;
    }

    public boolean check() {
        return this.matches;
    }

    public Class<?> getConfigFragment() {
        return config;
    }

    public boolean available() {
        Iterator i$ = sensors.iterator();
        while (i$.hasNext()) {
            if (new File((String) i$.next()).exists()) {
                return true;
            }
        }
        return false;
    }

    private int getTemperature() {
        int temp = Integer.MIN_VALUE;
        try {
            temp = Integer.parseInt(Utils.readFile(this.sensor).trim());
        } catch (NullPointerException e) {
        } catch (NumberFormatException e2) {
        }
        return temp;
    }
}
