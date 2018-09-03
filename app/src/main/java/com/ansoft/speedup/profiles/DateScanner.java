package com.ansoft.speedup.profiles;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import com.ansoft.speedup.profiles.condition.Condition;
import com.ansoft.speedup.profiles.condition.DateRangeState;
import com.ansoft.speedup.profiles.condition.DayOfWeekState;
import com.ansoft.speedup.profiles.condition.TimeRangeState;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.TreeMap;
import org.joda.time.DateTime;

public class DateScanner extends Scanner {
    public static final String INTENT_REFRESH_TIME = "setcpu.intent.action.REFRESH_TIME";
    private AlarmManager alarmManager;
    private Context context;
    BroadcastReceiver intentReceiver;
    private HashSet<Condition> next;
    private PendingIntent pendingIntent;
    private TreeMultimap<DateTime, Condition> timeMap;

    /* renamed from: com.ansoft.setthecpu.profiles.DateScanner.1 */
    class C01191 extends BroadcastReceiver {
        C01191() {
        }

        public void onReceive(Context context, Intent intent) {
            Iterator i$;
            if (intent.getAction().equals(DateScanner.INTENT_REFRESH_TIME)) {
                i$ = DateScanner.this.next.iterator();
                while (i$.hasNext()) {
                    ((Condition) i$.next()).updateState();
                }
                DateScanner.this.update();
            } else if (intent.getAction().equals("android.intent.action.TIME_SET") || intent.getAction().equals("android.intent.action.DATE_CHANGED") || intent.getAction().equals("android.intent.action.TIMEZONE_CHANGED")) {
                i$ = DateScanner.this.conditions.iterator();
                while (i$.hasNext()) {
                    ((Condition) i$.next()).updateState();
                }
                DateScanner.this.update();
            }
        }
    }

    public static class TreeMultimap<Key extends Comparable<?>, Entry> {
        private TreeMap<Key, List<Entry>> map;

        public TreeMultimap() {
            this.map = new TreeMap();
        }

        public boolean containsKey(Object key) {
            return this.map.containsKey(key);
        }

        public List<Entry> get(Object key) {
            return (List) this.map.get(key);
        }

        public Key first() throws NoSuchElementException {
            return (Key) this.map.firstKey();
        }

        public Set<Key> keySet() {
            return this.map.keySet();
        }

        public void clear() {
            this.map.clear();
        }

        public void add(Key key, Entry entry) {
            if (this.map.containsKey(key)) {
                ((List) this.map.get(key)).add(entry);
                return;
            }
            ArrayList<Entry> entryList = new ArrayList();
            entryList.add(entry);
            this.map.put(key, entryList);
        }
    }

    public DateScanner(Context context) {
        super(context);
        this.intentReceiver = new C01191();
        this.next = new HashSet();
        this.timeMap = new TreeMultimap();
        Intent intent = new Intent();
        intent.setAction(INTENT_REFRESH_TIME);
        this.pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(INTENT_REFRESH_TIME);
        intentFilter.addAction("android.intent.action.TIME_SET");
        intentFilter.addAction("android.intent.action.DATE_CHANGED");
        intentFilter.addAction("android.intent.action.TIMEZONE_CHANGED");
        this.alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        this.context = context;
        context.registerReceiver(this.intentReceiver, intentFilter);
    }

    public void start() {
        super.start();
        update();
    }

    public void stop() {
        super.stop();
        this.context.unregisterReceiver(this.intentReceiver);
        this.alarmManager.cancel(this.pendingIntent);
        this.timeMap.clear();
        this.next.clear();
    }

    private void update() {
        Iterator i$ = this.conditions.iterator();
        while (i$.hasNext()) {
            ((Condition) i$.next()).updateState();
        }
        this.timeMap = new TreeMultimap();
        i$ = this.conditions.iterator();
        while (i$.hasNext()) {
            Condition condition = (Condition) i$.next();
            if (condition instanceof TimeRangeState) {
                this.timeMap.add(((TimeRangeState) condition).getNextDateTime(), condition);
            } else if (condition instanceof DayOfWeekState) {
                DateTime add = ((DayOfWeekState) condition).getNextDateTime();
                if (add != null) {
                    this.timeMap.add(add, condition);
                }
            } else if (condition instanceof DateRangeState) {
                this.timeMap.add(((DateRangeState) condition).getNextDateTime(), condition);
            }
        }
        try {
            this.next = new HashSet(this.timeMap.get(this.timeMap.first()));
            if (((DateTime) this.timeMap.first()).getMillis() > System.currentTimeMillis()) {
                this.alarmManager.set(1, ((DateTime) this.timeMap.first()).getMillis(), this.pendingIntent);
            }
        } catch (NoSuchElementException e) {
            this.alarmManager.cancel(this.pendingIntent);
            this.next.clear();
        }
        this.updater.onScannerUpdate();
    }
}
