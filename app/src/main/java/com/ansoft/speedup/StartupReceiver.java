package com.ansoft.speedup;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

public class StartupReceiver extends BroadcastReceiver {
    public void onReceive(Context c, Intent intent) {
        start(c);
    }

    private void start(Context c) {
        c.startService(new Intent().setComponent(new ComponentName(c.getPackageName(), StartupService.class.getName())));
    }
}
