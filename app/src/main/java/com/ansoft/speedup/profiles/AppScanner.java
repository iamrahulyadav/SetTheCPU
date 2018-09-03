package com.ansoft.speedup.profiles;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.os.Build.VERSION;
import android.os.Bundle;
import com.ansoft.speedup.LogcatBuffer;
import com.ansoft.speedup.LogcatBuffer.OnLineReadListener;
import com.ansoft.speedup.profiles.condition.AppState;
import com.ansoft.speedup.profiles.condition.Condition;
import java.util.HashSet;
import java.util.Iterator;
import java.util.regex.Pattern;

public class AppScanner extends Scanner implements OnLineReadListener {
    private static final String activity_launch_time = "activity_launch_time";
    private static final String am_create_activity = "am_create_activity";
    private static final String am_resume_activity = "am_resume_activity";
    private static final String command = "-b events -v tag activity_launch_time:I am_create_activity:I am_resume_activity:I *:S";
    private static final String commandRegex = "[\\s/:,\\[\\]]+";
    private String activityName;
    private LogcatBuffer buffer;
    private Bundle bundleFalse;
    private Bundle bundleTrue;
    private Pattern commandPattern;
    private String eventName;
    private Intent intent;
    private HashSet<String> launcherActivities;
    private int offset;
    private String packageName;
    private String[] f16s;

    public AppScanner(Context context) {
        super(context);
        this.offset = 0;
        if (VERSION.SDK_INT > 16) {
            this.offset = 1;
        }
        this.bundleTrue = new Bundle();
        this.bundleTrue.putBoolean("appIsLaunched", true);
        this.bundleFalse = new Bundle();
        this.bundleFalse.putBoolean("appIsLaunched", false);
        this.commandPattern = Pattern.compile(commandRegex);
        this.f16s = new String[7];
        this.intent = new Intent();
        this.intent.setAction("android.intent.action.MAIN");
        this.intent.addCategory("android.intent.category.LAUNCHER");
    }

    public void start() {
        super.start();
        this.buffer = new LogcatBuffer(command, this.context);
        this.buffer.setOnLineReadListener(this);
        this.launcherActivities = new HashSet();
        Intent mainIntent = new Intent("android.intent.action.MAIN", null);
        mainIntent.addCategory("android.intent.category.LAUNCHER");
        for (ResolveInfo resolveInfo : this.context.getPackageManager().queryIntentActivities(mainIntent, 0)) {
            Iterator i$ = this.conditions.iterator();
            while (i$.hasNext()) {
                if (getPackageFromCondition((AppState) ((Condition) i$.next())).equals(resolveInfo.activityInfo.packageName)) {
                    this.launcherActivities.add(new ComponentName(resolveInfo.activityInfo.packageName, resolveInfo.activityInfo.name).flattenToShortString().split("/")[1]);
                }
            }
        }
    }

    public void stop() {
        super.stop();
        if (this.buffer.isAlive()) {
            this.buffer.stop();
        }
    }

    public boolean isAlive() {
        return this.buffer.isAlive();
    }

    public void onLineRead(String read) {
        boolean updated = false;
        if (read == null) {
            this.buffer.stop();
            this.buffer = new LogcatBuffer(command, this.context);
            this.buffer.setOnLineReadListener(this);
            return;
        }
        this.packageName = getPackageName(read);
        this.activityName = getActivityName(read);
        this.eventName = getEventName(read);
        if (this.eventName.equals(am_resume_activity) || this.eventName.equals(activity_launch_time) || this.eventName.equals(am_create_activity)) {
            Iterator i$ = this.conditions.iterator();
            while (i$.hasNext()) {
                Condition c = (Condition) i$.next();
                if (getPackageFromCondition((AppState) c).equals(this.packageName)) {
                    if (getMatchPackageOnlyFromCondition((AppState) c) || getActivityFromCondition((AppState) c).equals(this.activityName)) {
                        if (c.updateState(this.bundleTrue)) {
                            updated = true;
                        }
                    } else if (this.launcherActivities.contains(this.activityName) && c.updateState(this.bundleFalse)) {
                        updated = true;
                    }
                } else if (c.updateState(this.bundleFalse)) {
                    updated = true;
                }
            }
        }
        if (updated && this.updater != null) {
            this.updater.onScannerUpdate();
        }
    }

    private String getActivityFromCondition(AppState c) {
        return c.getActivity().split("/")[1];
    }

    private String getPackageFromCondition(AppState c) {
        return c.getActivity().split("/")[0];
    }

    private boolean getMatchPackageOnlyFromCondition(AppState c) {
        return c.getMatchPackageOnly();
    }

    private String getEventName(String read) {
        return this.commandPattern.split(read, 3)[1];
    }

    private String getActivityName(String read) {
        this.f16s = this.commandPattern.split(read, 7);
        if (this.f16s[1].equals(activity_launch_time)) {
            return this.f16s[this.offset + 4];
        }
        return this.f16s[this.offset + 5];
    }

    private String getPackageName(String read) {
        this.f16s = this.commandPattern.split(read, 7);
        if (this.f16s[1].equals(activity_launch_time)) {
            return this.f16s[this.offset + 3];
        }
        return this.f16s[this.offset + 4];
    }
}
