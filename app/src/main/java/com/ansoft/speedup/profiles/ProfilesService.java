package com.ansoft.speedup.profiles;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.ansoft.speedup.fragment.ProfilesFragment;
import com.ansoft.speedup.profiles.Scanner.ScannerUpdater;
import com.ansoft.speedup.profiles.action.Action;
import com.ansoft.speedup.profiles.action.ActionPerformer;
import com.ansoft.speedup.profiles.action.CpuFrequencyAction;
import com.ansoft.speedup.profiles.action.CpuGovernorAction;
import com.ansoft.speedup.profiles.action.IoSchedulerAction;
import com.ansoft.speedup.profiles.condition.Condition;
import com.ansoft.speedup.profiles.condition.Condition.Deserializer;
import com.ansoft.speedup.profiles.condition.Condition.Multimap;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.TreeSet;

public class ProfilesService extends Service {
    private TreeSet<Profile> activeProfiles;
    Gson gson;
    private Multimap<String, Condition> intentMap;
    BroadcastReceiver intentReceiver;
    int mRetval;
    private ActionPerformer performer;
    private ProfileList profiles;
    private Multimap<Class<? extends Scanner>, Condition> scannerMap;
    private HashSet<Scanner> scanners;
    private Updater updater;
    class C01281 extends BroadcastReceiver {
        C01281() {
        }

        public void onReceive(Context context, Intent intent) {
            if (ProfilesService.this.intentMap.containsKey(intent.getAction())) {
                boolean stateChanged = false;
                for (Condition condition : ProfilesService.this.intentMap.get(intent.getAction())) {
                    if (condition.updateState(intent)) {
                        stateChanged = true;
                    }
                }
                if (stateChanged) {
                    ProfilesService.this.update();
                }
            }
        }
    }

    private class Updater implements ScannerUpdater {
        private Updater() {
        }

        public void onScannerUpdate() {
            ProfilesService.this.update();
        }
    }

    public ProfilesService() {
        this.scanners = null;
        this.mRetval = 1;
        this.intentReceiver = new C01281();
    }

    public void onCreate() {
        super.onCreate();
        this.activeProfiles = new TreeSet();
        this.performer = new ActionPerformer(this);
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Condition.class, new Deserializer());
        gsonBuilder.registerTypeAdapter(Action.class, new Action.Deserializer());
        this.gson = gsonBuilder.create();
        this.updater = new Updater();
        this.profiles = new ProfileList();
        File file = new File(getDir(ProfilesFragment.PROFILES_DIR, 0).getAbsolutePath() + "/" + ProfilesFragment.PROFILES_FILE);
        try {
            Iterator i$;
            String str;
            this.profiles = (ProfileList) this.gson.fromJson(new FileReader(file), ProfileList.class);
            IntentFilter intentFilter = new IntentFilter();
            try {
                unregisterReceiver(this.intentReceiver);
            } catch (IllegalArgumentException e) {
            }
            if (this.scanners != null) {
                i$ = this.scanners.iterator();
                while (i$.hasNext()) {
                    ((Scanner) i$.next()).stop();
                }
            }
            this.scanners = new HashSet();
            if (this.profiles == null) {
                this.profiles = new ProfileList();
            }
            this.intentMap = this.profiles.getIntents();
            this.scannerMap = this.profiles.getScanners();
            for (String key : this.intentMap.keySet()) {
                intentFilter.addAction(key);
            }
            registerReceiver(this.intentReceiver, intentFilter);
            for (Class<? extends Scanner> key2 : this.scannerMap.keySet()) {
                try {
                    Scanner scanner = (Scanner) key2.getConstructor(new Class[]{Context.class}).newInstance(new Object[]{this});
                    for (Condition condition : this.scannerMap.get(key2)) {
                        scanner.addCondition(condition);
                    }
                    scanner.setScannerUpdater(this.updater);
                    this.scanners.add(scanner);
                } catch (InstantiationException e2) {
                    e2.printStackTrace();
                    this.mRetval = 2;
                    return;
                } catch (IllegalAccessException e3) {
                    e3.printStackTrace();
                    this.mRetval = 2;
                    return;
                } catch (NoSuchMethodException e4) {
                    e4.printStackTrace();
                    this.mRetval = 2;
                    return;
                } catch (IllegalArgumentException e5) {
                    e5.printStackTrace();
                    this.mRetval = 2;
                    return;
                } catch (InvocationTargetException e6) {
                    e6.printStackTrace();
                    this.mRetval = 2;
                    return;
                }
            }
            i$ = this.scanners.iterator();
            while (i$.hasNext()) {
                ((Scanner) i$.next()).start();
            }
            SharedPreferences settings = getSharedPreferences("SpeedUp", 0);
            Profile defaultProfile = new Profile();
            defaultProfile.setPriority(-1);
            defaultProfile.setName("Default");
            ArrayList<Action> defaultList = new ArrayList();
            Action defaultCpu = new CpuFrequencyAction();
            Action defaultGovernor = new CpuGovernorAction();
            Action defaultIo = new IoSchedulerAction();
            Bundle bundleCpu = new Bundle();
            try {
                str = "maxKHz";
                bundleCpu.putInt(str, settings.getInt("max", Integer.parseInt(this.performer.getCpufreq().getMax().trim())));
                str = "minKHz";
                bundleCpu.putInt(str, settings.getInt("min", Integer.parseInt(this.performer.getCpufreq().getMax().trim())));
            } catch (NullPointerException e7) {
            } catch (NumberFormatException e8) {
            }
            Bundle bundleGovernor = new Bundle();
            try {
                str = "governor";
                bundleGovernor.putString(str, settings.getString("stringGovernor", this.performer.getCpufreq().getGovernor()));
                if (bundleGovernor.getString("governor").equals("driver")) {
                    bundleGovernor.putString("governor", null);
                }
            } catch (NullPointerException e9) {
            } catch (NumberFormatException e10) {
            }
            Bundle bundleScheduler = new Bundle();
            try {
                str = "scheduler";
                bundleScheduler.putString(str, settings.getString("scheduler", this.performer.getCpufreq().getIoScheduler()));
            } catch (NullPointerException e11) {
            } catch (NumberFormatException e12) {
            }
            defaultCpu.set(bundleCpu);
            defaultGovernor.set(bundleGovernor);
            defaultIo.set(bundleScheduler);
            defaultList.add(defaultCpu);
            defaultList.add(defaultGovernor);
            defaultList.add(defaultIo);
            defaultProfile.setActionList(defaultList);
            this.profiles.addProfile(defaultProfile);
            update();
        } catch (JsonSyntaxException e13) {
            e13.printStackTrace();
            this.mRetval = 2;
        } catch (JsonIOException e14) {
            e14.printStackTrace();
            this.mRetval = 2;
        } catch (NullPointerException e15) {
            e15.printStackTrace();
            this.mRetval = 2;
        } catch (FileNotFoundException e16) {
            e16.printStackTrace();
            this.mRetval = 2;
        }
    }

    public void onDestroy() {
        super.onDestroy();
        if (this.scanners != null) {
            Iterator i$ = this.scanners.iterator();
            while (i$.hasNext()) {
                ((Scanner) i$.next()).stop();
            }
        }
        if (this.performer != null) {
            this.performer.stop();
        }
        try {
            unregisterReceiver(this.intentReceiver);
        } catch (IllegalArgumentException e) {
        }
    }

    public synchronized void update() {
        this.activeProfiles = this.profiles.getActiveProfiles();
        Iterator i$ = this.activeProfiles.iterator();
        while (i$.hasNext()) {
            Profile profile = (Profile) i$.next();
            Log.d("SpeedUp", "Active Profile: " + profile.getName());
            Log.d("SpeedUp", "Priority: " + profile.getPriority());
            Log.d("SpeedUp", "Actions: " + profile.getActionList());
            Log.d("SpeedUp", "Exclusive: " + profile.isExclusive());
        }
        evaluate();
        sendBroadcast(new Intent("SpeedUp.intent.action.updatewidget"));
    }

    public boolean evaluate() {
        return this.performer.perform(this.activeProfiles);
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        return this.mRetval;
    }

    public IBinder onBind(Intent intent) {
        return null;
    }
}
