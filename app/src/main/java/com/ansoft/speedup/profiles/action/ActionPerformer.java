package com.ansoft.speedup.profiles.action;

import android.app.NotificationManager;
import android.content.Context;
import android.widget.Toast;

import com.ansoft.speedup.profiles.Profile;
import com.ansoft.speedup.util.Cpufreq;
import java.util.HashSet;
import java.util.Set;

public class ActionPerformer {
    private Context context;
    private Cpufreq cpufreq;
    private NotificationManager nm;
    private HashSet<String> performedActions;

    public ActionPerformer(Context context) {
        this.context = context;
        this.cpufreq = new Cpufreq(context, true);
        this.nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    public boolean perform(Set<Profile> activeProfiles) {
        boolean cancel = true;
        boolean successful = false;
        this.performedActions = new HashSet();
        for (Profile profile : activeProfiles) {
            for (Action action : profile.getActionList()) {
                if (!this.performedActions.contains(action.getTypeName())) {
                    if (action.getTypeName().equals("Notification")) {
                        cancel = false;
                    }
                    if (!action.perform(this, profile)) {
                        successful = false;
                    }
                    this.performedActions.add(action.getTypeName());
                }
            }
        }
        if (cancel) {
            this.nm.cancel(1);
        }
        return successful;
    }

    public Cpufreq getCpufreq() {
        return this.cpufreq;
    }

    public Context getContext() {
        return this.context;
    }

    public boolean hasRootAccess() {
        return false;
    }

    public void stop() {
        this.nm.cancel(1);
        if (this.cpufreq != null) {
            this.cpufreq.kill();
        }
    }
}
