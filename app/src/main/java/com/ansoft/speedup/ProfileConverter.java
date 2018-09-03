package com.ansoft.speedup;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import com.ansoft.speedup.profiles.Profile;
import com.ansoft.speedup.profiles.ProfileList;
import com.ansoft.speedup.profiles.action.CpuFrequencyAction;
import com.ansoft.speedup.profiles.action.CpuGovernorAction;
import com.ansoft.speedup.profiles.action.NotificationAction;
import com.ansoft.speedup.profiles.condition.BatteryLevelState;
import com.ansoft.speedup.profiles.condition.BatteryTempState;
import com.ansoft.speedup.profiles.condition.CallState;
import com.ansoft.speedup.profiles.condition.ChargeState;
import com.ansoft.speedup.profiles.condition.Condition;
import com.ansoft.speedup.profiles.condition.CpuTempState;
import com.ansoft.speedup.profiles.condition.ScreenState;
import com.ansoft.speedup.profiles.condition.TimeRangeState;

public class ProfileConverter {
    Context context;

    public ProfileConverter(Context context) {
        this.context = context;
    }

    public ProfileList convertDatabase() {
        return convertDatabase(null);
    }

    public ProfileList convertDatabase(String dbName) {
        DBHelper db;
        ProfileList profileList = new ProfileList();
        if (dbName == null) {
            db = new DBHelper(this.context);
        } else {
            db = new DBHelper(this.context, dbName);
        }
        Cursor cursor = db.getAllProfilesByPriority();
        cursor.moveToFirst();
        int currentPriority = ((int) Math.floor(((double) cursor.getCount()) / 2.0d)) + 50;
        if (currentPriority > 100) {
            currentPriority = 100;
        }
        boolean notify = this.context.getSharedPreferences("SpeedUp", 0).getBoolean("profileNotifications", false);
        while (!cursor.isAfterLast() && currentPriority >= 0) {
            int priority = currentPriority;
            boolean enabled = cursor.getInt(2) != 0;
            int profileid = cursor.getInt(3);
            int max = cursor.getInt(4);
            int min = cursor.getInt(5);
            String governor = cursor.getString(6);
            String icon = null;
            Profile profile = new Profile();
            profile.setPriority(priority);
            profile.setEnabled(enabled);
            profile.setExclusive(true);
            ChargeState chargeState;
            Bundle bundle;
            if (profileid == 0) {
                chargeState = new ChargeState();
                bundle = new Bundle();
                bundle.putString("mode", "Any");
                chargeState.set(bundle);
                profile.getBaseCondition().addChild(chargeState);
                profile.setName(chargeState.getFormattedName(this.context));
                icon = "charge";
            } else if (profileid == 1) {
                chargeState = new ChargeState();
                bundle = new Bundle();
                bundle.putString("mode", "AC");
                chargeState.set(bundle);
                profile.getBaseCondition().addChild(chargeState);
                profile.setName(chargeState.getFormattedName(this.context));
                icon = "charge";
            } else if (profileid == 2) {
                chargeState = new ChargeState();
                bundle = new Bundle();
                bundle.putString("mode", "USB");
                chargeState.set(bundle);
                profile.getBaseCondition().addChild(chargeState);
                profile.setName(chargeState.getFormattedName(this.context));
                icon = "charge";
            } else if (profileid == 4) {
                BatteryLevelState batteryState = new BatteryLevelState();
                bundle = new Bundle();
                int level = cursor.getInt(7);
                if (level > 100) {
                    level = 100;
                    bundle.putString("inequality", "<=");
                } else {
                    bundle.putString("inequality", "<");
                }
                bundle.putInt("level", level);
                batteryState.set(bundle);
                profile.getBaseCondition().addChild(batteryState);
                profile.setName(batteryState.getFormattedName(this.context));
                icon = "battery";
            } else if (profileid == 5) {
                BatteryTempState tempState = new BatteryTempState();
                bundle = new Bundle();
                bundle.putString("inequality", ">");
                bundle.putInt("temp", cursor.getInt(7));
                tempState.set(bundle);
                profile.getBaseCondition().addChild(tempState);
                profile.setName(tempState.getFormattedName(this.context));
                icon = "failsafe";
            } else if (profileid == 6) {
                CpuTempState tempState = new CpuTempState();
                bundle = new Bundle();
                bundle.putString("inequality", ">");
                bundle.putInt("temp", cursor.getInt(7));
                tempState.set(bundle);
                profile.getBaseCondition().addChild(tempState);
                profile.setName(tempState.getFormattedName(this.context));
                icon = "failsafe";
            } else if (profileid == 3) {
                Condition screenState = new ScreenState();
                bundle = new Bundle();
                bundle.putBoolean("on", false);
                screenState.set(bundle);
                profile.getBaseCondition().addChild(screenState);
                profile.setName(screenState.getFormattedName(this.context));
            } else if (profileid == 7) {
                Condition timeState = new TimeRangeState();
                bundle = new Bundle();
                bundle.putString("startTime", cursor.getInt(7) + ":" + cursor.getInt(8));
                bundle.putString("endTime", cursor.getInt(9) + ":" + cursor.getInt(10));
                timeState.set(bundle);
                profile.getBaseCondition().addChild(timeState);
                profile.setName(timeState.getFormattedName(this.context));
                icon = "time";
            } else if (profileid == 8) {
                CallState callState = new CallState();
                bundle = new Bundle();
                bundle.putBoolean("inCall", true);
                callState.set(bundle);
                profile.getBaseCondition().addChild(callState);
                profile.setName(callState.getFormattedName(this.context));
                icon = "call";
            } else {
                cursor.moveToNext();
            }
            Bundle bundleFreq = new Bundle();
            Bundle bundleGov = new Bundle();
            CpuFrequencyAction cpuFrequency = new CpuFrequencyAction();
            CpuGovernorAction cpuGovernor = new CpuGovernorAction();
            bundleFreq.putInt("maxKHz", max);
            bundleFreq.putInt("minKHz", min);
            bundleGov.putString("governor", governor);
            cpuFrequency.set(bundleFreq);
            cpuGovernor.set(bundleGov);
            profile.getActionList().add(cpuFrequency);
            if (!governor.equals("driver")) {
                profile.getActionList().add(cpuGovernor);
            }
            if (notify && profileid != 3) {
                Bundle bundleNotify = new Bundle();
                NotificationAction notification = new NotificationAction();
                bundleNotify.putString("icon", icon);
                bundleNotify.putBoolean("persistent", true);
                notification.set(bundleNotify);
                profile.getActionList().add(notification);
            }
            profileList.addProfile(profile);
            cursor.moveToNext();
            currentPriority--;
        }
        return profileList;
    }
}
