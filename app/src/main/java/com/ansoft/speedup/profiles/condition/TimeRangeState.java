package com.ansoft.speedup.profiles.condition;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.ansoft.speedup.R;
import com.ansoft.speedup.profiles.DateScanner;
import com.ansoft.speedup.profiles.conditionconfig.TimeRangeConfigFragment;
import java.text.DateFormat;
import org.joda.time.DateTime;
import org.joda.time.LocalTime;
import org.joda.time.ReadableInstant;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class TimeRangeState extends Condition {
    public static final transient Class<?> config;
    public static final transient int format = 2131492909;
    public static final transient int name = 2131492893;
    private String endTime;
    private transient DateTimeFormatter formatter;
    private transient boolean init;
    private transient LocalTime localTimeEnd;
    private transient LocalTime localTimeStart;
    private String startTime;
    private transient boolean withinRange;

    static {
        config = TimeRangeConfigFragment.class;
    }

    public TimeRangeState() {
        this.withinRange = false;
        this.init = false;
        this.startTime = "00:00";
        this.endTime = "00:00";
        this.type = "TimeRange";
        this.category = 1;
        this.scanners.add(DateScanner.class);
        this.formatter = DateTimeFormat.forPattern("HH:mm");
        try {
            this.localTimeStart = this.formatter.parseLocalTime(this.startTime);
            this.localTimeEnd = this.formatter.parseLocalTime(this.endTime);
        } catch (IllegalArgumentException e) {
            Log.w("SpeedUp", "JSON parse error: Invalid time specified in TimeRange Condition.");
            this.localTimeStart = new LocalTime();
            this.localTimeEnd = new LocalTime();
        }
    }

    public String getName(Context context) {
        return context.getResources().getString(R.string.condition_time);
    }

    public String getFormattedName(Context context) {
        init();
        DateFormat df = android.text.format.DateFormat.getTimeFormat(context);
        CharSequence formatStart = df.format(this.localTimeStart.toDateTimeToday().toDate());
        CharSequence formatEnd = df.format(this.localTimeEnd.toDateTimeToday().toDate());
        return String.format(context.getResources().getString(R.string.condition_format_time), new Object[]{formatStart, formatEnd});
    }

    public Class<?> getConfigFragment() {
        return config;
    }

    public boolean set(Bundle args) {
        this.init = false;
        if (!args.containsKey("startTime") || !args.containsKey("endTime")) {
            return false;
        }
        this.startTime = args.getString("startTime");
        this.endTime = args.getString("endTime");
        return true;
    }

    public Bundle get() {
        Bundle bundle = new Bundle();
        bundle.putString("startTime", this.startTime);
        bundle.putString("endTime", this.endTime);
        return bundle;
    }

    public boolean updateState() {
        init();
        DateTime dateTimeStart = this.localTimeStart.toDateTimeToday();
        DateTime dateTimeEnd = this.localTimeEnd.toDateTimeToday().plusMinutes(1);
        if (dateTimeStart.isBeforeNow() && dateTimeEnd.isAfterNow()) {
            this.withinRange = true;
            return true;
        }
        if (dateTimeStart.isAfter((ReadableInstant) dateTimeEnd)) {
            if (dateTimeStart.minusDays(1).isBeforeNow() && dateTimeEnd.isAfterNow()) {
                this.withinRange = true;
                return true;
            } else if (dateTimeStart.isBeforeNow() && dateTimeEnd.plusDays(1).isAfterNow()) {
                this.withinRange = true;
                return true;
            }
        }
        this.withinRange = false;
        return false;
    }

    public DateTime getNextDateTime() {
        init();
        DateTime dateTimeStart = this.localTimeStart.toDateTimeToday();
        DateTime dateTimeEnd = this.localTimeEnd.toDateTimeToday().plusMinutes(1);
        if (dateTimeStart.isAfter((ReadableInstant) dateTimeEnd)) {
            if (dateTimeEnd.isAfterNow()) {
                return dateTimeEnd;
            }
            if (dateTimeStart.isAfterNow()) {
                return dateTimeStart;
            }
            return dateTimeEnd.plusDays(1);
        } else if (dateTimeStart.isAfterNow()) {
            return dateTimeStart;
        } else {
            return !dateTimeEnd.isAfterNow() ? dateTimeStart.plusDays(1) : dateTimeEnd;
        }
    }

    private void init() {
        if (!this.init) {
            try {
                this.localTimeStart = this.formatter.parseLocalTime(this.startTime);
                this.localTimeEnd = this.formatter.parseLocalTime(this.endTime);
            } catch (IllegalArgumentException e) {
                Log.w("SpeedUp", "JSON parse error: Invalid time specified in TimeRange Condition.");
                this.localTimeStart = new LocalTime();
                this.localTimeEnd = new LocalTime();
            }
            this.init = true;
        }
    }

    public boolean check() {
        return this.withinRange;
    }
}
