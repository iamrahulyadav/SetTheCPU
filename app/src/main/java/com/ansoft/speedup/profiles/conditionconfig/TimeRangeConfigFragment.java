package com.ansoft.speedup.profiles.conditionconfig;

import android.annotation.SuppressLint;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.view.View;
import android.widget.TimePicker;

import com.ansoft.speedup.R;
import com.ansoft.speedup.profiles.condition.Condition;
import com.ansoft.speedup.profiles.condition.ConditionPickerFragment.OnConditionAddListener;
import com.ansoft.speedup.profiles.condition.TimeRangeState;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class TimeRangeConfigFragment extends ConditionConfigFragment {
    public static int name;
    Condition condition;
    TimePicker datePickerFrom;
    TimePicker datePickerTo;
    View view;

    public TimeRangeConfigFragment(){}

    /* renamed from: com.ansoft.setthecpu.profiles.conditionconfig.TimeRangeConfigFragment.1 */
    class C01381 implements OnClickListener {
        C01381() {
        }

        public void onClick(DialogInterface dialog, int whichButton) {
            TimeRangeConfigFragment.this.onPositiveClick();
            TimeRangeConfigFragment.this.saveCondition();
        }
    }

    static {
        name = R.string.condition_time;
    }

    @SuppressLint("ValidFragment")
    public TimeRangeConfigFragment(Condition parentCondition, Condition condition, View parentView, OnConditionAddListener listener) {
        super(parentCondition, condition, parentView, listener);
        this.condition = condition;
    }

    @SuppressLint("ValidFragment")
    public TimeRangeConfigFragment(Condition condition, View view, OnConditionSaveListener listener) {
        super(condition, view, listener);
        this.condition = condition;
    }

    protected void onPositiveClick() {
        Bundle bundle = new Bundle();
        bundle.putString("startTime", this.datePickerFrom.getCurrentHour() + ":" + this.datePickerFrom.getCurrentMinute());
        bundle.putString("endTime", this.datePickerTo.getCurrentHour() + ":" + this.datePickerTo.getCurrentMinute());
        this.condition.set(bundle);
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = getActivity().getLayoutInflater().inflate(R.layout.time_range_config, null);
        this.datePickerFrom = (TimePicker) view.findViewById(R.id.timePickerFrom);
        this.datePickerTo = (TimePicker) view.findViewById(R.id.timePickerTo);
        Bundle bundle = ((TimeRangeState) this.condition).get();
        DateTimeFormatter formatter = DateTimeFormat.forPattern("HH:mm");
        try {
            this.datePickerFrom.setCurrentHour(Integer.valueOf(formatter.parseDateTime(bundle.getString("startTime")).getHourOfDay()));
            this.datePickerFrom.setCurrentMinute(Integer.valueOf(formatter.parseDateTime(bundle.getString("startTime")).getMinuteOfHour()));
            this.datePickerTo.setCurrentHour(Integer.valueOf(formatter.parseDateTime(bundle.getString("endTime")).getHourOfDay()));
            this.datePickerTo.setCurrentMinute(Integer.valueOf(formatter.parseDateTime(bundle.getString("endTime")).getMinuteOfHour()));
        } catch (NullPointerException e) {
        }
        new Builder(getActivity()).setView(view).setTitle(this.condition.getName(getActivity())).setPositiveButton(R.string.save, new C01381()).setView(view).create().show();
    }
}
