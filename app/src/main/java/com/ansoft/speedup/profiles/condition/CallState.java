package com.ansoft.speedup.profiles.condition;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;

import com.ansoft.speedup.R;
import com.ansoft.speedup.profiles.conditionconfig.CallStateConfigFragment;

public class CallState extends Condition {
    public static final transient Class<?> config;
    public static final transient int format = 2131492912;
    public static final transient int name = 2131492896;
    private transient boolean currentlyInCall;
    private boolean inCall;

    static {
        config = CallStateConfigFragment.class;
    }

    public CallState() {
        this.currentlyInCall = false;
        this.inCall = true;
        this.type = "Call";
        this.category = 1;
        this.intents.add("android.intent.action.PHONE_STATE");
    }

    public String getName(Context context) {
        return context.getResources().getString(R.string.condition_call);
    }

    public String getFormattedName(Context context) {
        if (this.inCall) {
            return String.format(context.getResources().getString(R.string.condition_format_call), new Object[]{context.getResources().getString(R.string.condition_in)});
        }
        return String.format(context.getResources().getString(R.string.condition_format_call), new Object[]{context.getResources().getString(R.string.condition_not_in)});
    }

    public Class<?> getConfigFragment() {
        return config;
    }

    public boolean set(Bundle args) {
        if (!args.containsKey("inCall")) {
            return false;
        }
        this.inCall = args.getBoolean("inCall");
        return true;
    }

    public Bundle get() {
        Bundle bundle = new Bundle();
        bundle.putBoolean("inCall", this.inCall);
        return bundle;
    }

    public boolean updateState(Intent intent) {
        if (!intent.getAction().equals("android.intent.action.PHONE_STATE")) {
            return false;
        }
        boolean newInCall;
        String extra = intent.getStringExtra("state");
        if (extra.equals(TelephonyManager.EXTRA_STATE_IDLE)) {
            newInCall = false;
        } else if (extra.equals(TelephonyManager.EXTRA_STATE_OFFHOOK) || extra.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
            newInCall = true;
        } else {
            newInCall = false;
        }
        if (this.currentlyInCall == newInCall) {
            return false;
        }
        this.currentlyInCall = newInCall;
        return true;
    }

    public boolean check() {
        return this.inCall == this.currentlyInCall;
    }
}
