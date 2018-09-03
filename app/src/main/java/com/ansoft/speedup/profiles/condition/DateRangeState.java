package com.ansoft.speedup.profiles.condition;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.ansoft.speedup.R;
import com.ansoft.speedup.profiles.DateScanner;
import com.ansoft.speedup.profiles.conditionconfig.DateRangeConfigFragment;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.ReadableInstant;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class DateRangeState extends Condition {
    public static final transient Class<?> config;
    public static final transient int format = 2131492910;
    public static final transient int name = 2131492894;
    private String endDate;
    private transient DateTimeFormatter formatter;
    private transient boolean init;
    private transient LocalDate localDateEnd;
    private transient LocalDate localDateStart;
    private String startDate;
    private transient boolean withinRange;

    static {
        config = DateRangeConfigFragment.class;
    }

    public DateRangeState() {
        this.withinRange = false;
        this.init = false;
        this.startDate = "01/01";
        this.endDate = "01/01";
        this.type = "DateRange";
        this.category = 1;
        this.scanners.add(DateScanner.class);
        this.formatter = DateTimeFormat.forPattern("dd/MM");
    }

    public String getName(Context context) {
        return context.getResources().getString(R.string.condition_date_range);
    }

    public String getFormattedName(Context context) {
        init();
        DateTimeFormatter df = DateTimeFormat.forPattern("MMM dd");
        CharSequence formatStart = df.print(this.localDateStart);
        CharSequence formatEnd = df.print(this.localDateEnd.minusDays(1));
        return String.format(context.getResources().getString(R.string.condition_format_date_range), new Object[]{formatStart, formatEnd});
    }

    public Class<?> getConfigFragment() {
        return config;
    }

    public boolean set(Bundle args) {
        this.init = false;
        if (!args.containsKey("startDate") || !args.containsKey("endDate")) {
            return false;
        }
        this.startDate = args.getString("startDate");
        this.endDate = args.getString("endDate");
        return true;
    }

    public Bundle get() {
        Bundle bundle = new Bundle();
        bundle.putString("startDate", this.startDate);
        bundle.putString("endDate", this.endDate);
        return bundle;
    }

    public boolean updateState() {
        init();
        DateTime dateTimeStart = this.localDateStart.toDateTimeAtStartOfDay();
        DateTime dateTimeEnd = this.localDateEnd.toDateTimeAtStartOfDay();
        dateTimeStart = dateTimeStart.withYear(DateTime.now().getYear());
        dateTimeEnd = dateTimeEnd.withYear(DateTime.now().getYear());
        if (dateTimeStart.isBeforeNow() && dateTimeEnd.isAfterNow()) {
            this.withinRange = true;
            return true;
        }
        if (dateTimeStart.isAfter((ReadableInstant) dateTimeEnd)) {
            if (dateTimeStart.minusYears(1).isBeforeNow() && dateTimeEnd.isAfterNow()) {
                this.withinRange = true;
                return true;
            } else if (dateTimeStart.isBeforeNow() && dateTimeEnd.plusYears(1).isAfterNow()) {
                this.withinRange = true;
                return true;
            }
        }
        this.withinRange = false;
        return false;
    }

    public DateTime getNextDateTime() {
        init();
        DateTime dateTimeStart = this.localDateStart.toDateTimeAtStartOfDay();
        DateTime dateTimeEnd = this.localDateEnd.toDateTimeAtStartOfDay();
        dateTimeStart = dateTimeStart.withYear(DateTime.now().getYear());
        dateTimeEnd = dateTimeEnd.withYear(DateTime.now().getYear());
        if (dateTimeStart.isAfter((ReadableInstant) dateTimeEnd)) {
            if (dateTimeEnd.isAfterNow()) {
                return dateTimeEnd;
            }
            if (dateTimeStart.isAfterNow()) {
                return dateTimeStart;
            }
            return dateTimeEnd.plusYears(1);
        } else if (dateTimeStart.isAfterNow()) {
            return dateTimeStart;
        } else {
            return !dateTimeEnd.isAfterNow() ? dateTimeStart.plusYears(1) : dateTimeEnd;
        }
    }

    private void init() {
        if (!this.init) {
            try {
                this.localDateStart = this.formatter.parseLocalDate(this.startDate);
                this.localDateEnd = this.formatter.parseLocalDate(this.endDate).plusDays(1);
            } catch (IllegalArgumentException e) {
                Log.w("SpeedUp", "JSON parse error: Invalid date specified in DateRange Condition.");
                this.localDateStart = new LocalDate();
                this.localDateEnd = new LocalDate();
            }
            this.init = true;
        }
    }

    public boolean check() {
        return this.withinRange;
    }
}
