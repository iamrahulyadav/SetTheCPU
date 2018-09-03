package com.ansoft.speedup.profiles.condition;

import android.content.Context;
import android.content.Intent;

import com.ansoft.speedup.R;

public class UserPresentState extends Condition {
    public static final transient int format = 2131492889;
    public static final transient int name = 2131492889;
    private transient boolean screenIsUnlocked;

    public UserPresentState() {
        this.screenIsUnlocked = true;
        this.type = "ScreenUnlocked";
        this.category = 1;
        this.intents.add("android.intent.action.USER_PRESENT");
        this.intents.add("android.intent.action.SCREEN_OFF");
    }

    public String getName(Context context) {
        return context.getResources().getString(R.string.condition_screen_unlocked);
    }

    public String getFormattedName(Context context) {
        return context.getResources().getString(R.string.condition_screen_unlocked);
    }

    public boolean updateState(Intent intent) {
        boolean newOn = true;
        if (intent.getAction().equals("android.intent.action.USER_PRESENT")) {
            newOn = true;
        } else if (intent.getAction().equals("android.intent.action.SCREEN_OFF")) {
            newOn = false;
        }
        this.screenIsUnlocked = newOn;
        return true;
    }

    public boolean check() {
        return this.screenIsUnlocked;
    }
}
