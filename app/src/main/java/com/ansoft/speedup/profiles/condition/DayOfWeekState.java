package com.ansoft.speedup.profiles.condition;

import android.content.Context;
import android.os.Bundle;

import com.ansoft.speedup.R;
import com.ansoft.speedup.profiles.DateScanner;
import com.ansoft.speedup.profiles.conditionconfig.DayOfWeekConfigFragment;
import java.util.NoSuchElementException;
import java.util.TreeSet;
import org.joda.time.DateTime;

public class DayOfWeekState extends Condition {
    public static final transient Class<?> config;
    public static final transient int format = 2131492911;
    public static final transient int name = 2131492895;
    private boolean[] days;
    private transient boolean withinRange;

    static {
        config = DayOfWeekConfigFragment.class;
    }

    public DayOfWeekState() {
        this.withinRange = false;
        this.days = new boolean[]{false, false, false, false, false, false, false};
        this.type = "DayOfWeek";
        this.category = 1;
        this.scanners.add(DateScanner.class);
    }

    public String getName(Context context) {
        return context.getResources().getString(R.string.condition_dayofweek);
    }

    public String getFormattedName(Context context) {
        boolean hasDays = false;
        StringBuilder builder = new StringBuilder(48);
        builder.append(context.getResources().getString(R.string.condition_format_dayofweek));
        builder.append(" ");
        if (this.days[0]) {
            builder.append(context.getResources().getString(R.string.monday));
            builder.append(" ");
            hasDays = true;
        }
        if (this.days[1]) {
            builder.append(context.getResources().getString(R.string.tuesday));
            builder.append(" ");
            hasDays = true;
        }
        if (this.days[2]) {
            builder.append(context.getResources().getString(R.string.wednesday));
            builder.append(" ");
            hasDays = true;
        }
        if (this.days[3]) {
            builder.append(context.getResources().getString(R.string.thursday));
            builder.append(" ");
            hasDays = true;
        }
        if (this.days[4]) {
            builder.append(context.getResources().getString(R.string.friday));
            builder.append(" ");
            hasDays = true;
        }
        if (this.days[5]) {
            builder.append(context.getResources().getString(R.string.saturday));
            builder.append(" ");
            hasDays = true;
        }
        if (this.days[6]) {
            builder.append(context.getResources().getString(R.string.sunday));
            hasDays = true;
        }
        if (!hasDays) {
            builder.append(context.getResources().getString(R.string.none));
        }
        return builder.toString().trim();
    }

    public Class<?> getConfigFragment() {
        return config;
    }

    public boolean set(Bundle args) {
        if (!args.containsKey("days")) {
            return false;
        }
        if (args.getBooleanArray("days").length == 7) {
            this.days = args.getBooleanArray("days");
        }
        return true;
    }

    public Bundle get() {
        Bundle bundle = new Bundle();
        bundle.putBooleanArray("days", this.days);
        return bundle;
    }

    public boolean updateState() {
        DateTime now = DateTime.now();
        int i = 0;
        while (i < 6) {
            if (this.days[i] && now.getDayOfWeek() == i + 1) {
                this.withinRange = true;
                return true;
            }
            i++;
        }
        this.withinRange = false;
        return false;
    }

    public DateTime getNextDateTime() {
        if (this.withinRange) {
            return DateTime.now().plusDays(1).withTimeAtStartOfDay();
        }
        return calcNextDay(DateTime.now());
    }

    private DateTime calcNextDay(DateTime d) {
        TreeSet<DateTime> daySet = new TreeSet();
        for (int i = 0; i < 6; i++) {
            if (this.days[i]) {
                if (d.getDayOfWeek() >= i + 1) {
                    daySet.add(d.plusWeeks(1).withDayOfWeek(i + 1).withTimeAtStartOfDay());
                } else {
                    daySet.add(d.withDayOfWeek(i + 1).withTimeAtStartOfDay());
                }
            }
        }
        try {
            return (DateTime) daySet.first();
        } catch (NoSuchElementException e) {
            return null;
        }
    }

    public boolean check() {
        return this.withinRange;
    }
}
