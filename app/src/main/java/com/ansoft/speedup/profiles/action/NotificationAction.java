package com.ansoft.speedup.profiles.action;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat.Builder;

import com.ansoft.speedup.MainActivity;
import com.ansoft.speedup.R;
import com.ansoft.speedup.profiles.Profile;
import java.util.HashMap;
import org.achartengine.ChartFactory;

public class NotificationAction extends Action {
    public static final transient int NOTIFICATION_ID = 1;
    public static final transient int format = 2131492955;
    public static transient HashMap<String, Integer> iconMap = null;
    public static final transient int name = 2131492951;
    private transient Builder builder;
    private transient String content;
    private String icon;
    private transient NotificationManager nm;
    private boolean persistent;
    private transient PendingIntent pi;
    private String title;

    static {
        iconMap = new HashMap();
        iconMap.put("normal", Integer.valueOf(R.drawable.notify));
        iconMap.put("battery", Integer.valueOf(R.drawable.notify_battery));
        iconMap.put("call", Integer.valueOf(R.drawable.notify_call));
        iconMap.put("charge", Integer.valueOf(R.drawable.notify_charge));
        iconMap.put("failsafe", Integer.valueOf(R.drawable.notify_failsafe));
        iconMap.put("time", Integer.valueOf(R.drawable.notify_time));
    }

    public NotificationAction() {
        this.content = null;
        this.nm = null;
        this.pi = null;
        this.title = "";
        this.icon = "normal";
        this.persistent = true;
        this.type = "Notification";
    }

    public String getName(Context context) {
        return context.getResources().getString(R.string.action_show_notification);
    }

    public String getFormattedName(Context context) {
        return context.getResources().getString(R.string.action_format_show_notification);
    }

    public boolean perform(ActionPerformer performer, Profile profile) {
        if (this.pi == null) {
            this.pi = PendingIntent.getActivity(performer.getContext(), 0, new Intent(performer.getContext(), MainActivity.class), 0);
        }
        if (this.nm == null) {
            this.nm = (NotificationManager) performer.getContext().getSystemService(Context.NOTIFICATION_SERVICE);
        }
        String title = this.title;
        if (title.equals("") || title == null) {
            title = profile.getName();
        }
        if (this.content == null) {
            StringBuilder contentBuilder = new StringBuilder(48);
            for (Action a : profile.getActionList()) {
                if (!a.equals(this)) {
                    contentBuilder.append(a.getFormattedName(performer.getContext()));
                    contentBuilder.append(" ");
                }
                this.content = contentBuilder.toString();
            }
        }
        if (this.builder == null) {
            this.builder = new Builder(performer.getContext());
            this.builder.setTicker(title);
            this.builder.setSmallIcon(((Integer) iconMap.get(this.icon)).intValue());
            this.builder.setContentText(this.content);
            this.builder.setContentTitle(title);
            this.builder.setContentIntent(this.pi);
            this.builder.setOngoing(this.persistent);
            this.builder.setWhen(0);
            this.builder.setOnlyAlertOnce(true);
        }
        this.nm.notify(NOTIFICATION_ID, this.builder.getNotification());
        return true;
    }

    public boolean set(Bundle args) {
        if (!args.containsKey("icon") || !args.containsKey("persistent")) {
            return false;
        }
        this.icon = args.getString("icon");
        this.persistent = args.getBoolean("persistent");
        return true;
    }

    public Bundle get() {
        Bundle bundle = new Bundle();
        bundle.putString(ChartFactory.TITLE, this.title);
        bundle.putString("icon", this.icon);
        bundle.putBoolean("persistent", this.persistent);
        return bundle;
    }
}
