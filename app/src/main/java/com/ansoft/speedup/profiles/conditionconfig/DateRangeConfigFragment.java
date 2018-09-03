package com.ansoft.speedup.profiles.conditionconfig;

import android.annotation.SuppressLint;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;

import com.ansoft.speedup.R;
import com.ansoft.speedup.profiles.condition.Condition;
import com.ansoft.speedup.profiles.condition.ConditionPickerFragment.OnConditionAddListener;
import com.ansoft.speedup.profiles.condition.DateRangeState;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class DateRangeConfigFragment extends ConditionConfigFragment {
    public static int name;
    Condition condition;
    DatePicker datePickerFrom;
    DatePicker datePickerTo;
    View view;

    public DateRangeConfigFragment(){}

    /* renamed from: com.ansoft.setthecpu.profiles.conditionconfig.DateRangeConfigFragment.1 */
    class C01351 implements OnClickListener {
        C01351() {
        }

        public void onClick(DialogInterface dialog, int whichButton) {
            DateRangeConfigFragment.this.onPositiveClick();
            DateRangeConfigFragment.this.saveCondition();
        }
    }

    static {
        name = R.string.condition_date_range;
    }

    @SuppressLint("ValidFragment")
    public DateRangeConfigFragment(Condition parentCondition, Condition condition, View parentView, OnConditionAddListener listener) {
        super(parentCondition, condition, parentView, listener);
        this.condition = condition;
    }

    @SuppressLint("ValidFragment")
    public DateRangeConfigFragment(Condition condition, View view, OnConditionSaveListener listener) {
        super(condition, view, listener);
        this.condition = condition;
    }

    protected void onPositiveClick() {
        Bundle bundle = new Bundle();
        bundle.putString("startDate", this.datePickerFrom.getDayOfMonth() + "/" + (this.datePickerFrom.getMonth() + 1));
        bundle.putString("endDate", this.datePickerTo.getDayOfMonth() + "/" + (this.datePickerTo.getMonth() + 1));
        Log.d("SpeedUp", this.datePickerFrom.getDayOfMonth() + "/" + (this.datePickerFrom.getMonth() + 1));
        Log.d("SpeedUp", this.datePickerTo.getDayOfMonth() + "/" + (this.datePickerTo.getMonth() + 1));
        this.condition.set(bundle);
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = getActivity().getLayoutInflater().inflate(R.layout.date_range_config, null);
        this.datePickerFrom = (DatePicker) view.findViewById(R.id.datePickerFrom);
        this.datePickerTo = (DatePicker) view.findViewById(R.id.datePickerTo);
        try {
            this.datePickerFrom.setCalendarViewShown(false);
            this.datePickerTo.setCalendarViewShown(false);
        } catch (NoSuchMethodError e) {
        }
        Bundle bundle = ((DateRangeState) this.condition).get();
        DateTimeFormatter formatter = DateTimeFormat.forPattern("dd/MM");
        try {
            this.datePickerFrom.updateDate(DateTime.now().getYear(), formatter.parseDateTime(bundle.getString("startDate")).getMonthOfYear() - 1, formatter.parseDateTime(bundle.getString("startDate")).getDayOfMonth());
            this.datePickerTo.updateDate(DateTime.now().getYear(), formatter.parseDateTime(bundle.getString("endDate")).getMonthOfYear() - 1, formatter.parseDateTime(bundle.getString("endDate")).getDayOfMonth());
        } catch (NullPointerException e2) {
        }
        new Builder(getActivity()).setView(view).setTitle(this.condition.getName(getActivity())).setPositiveButton(R.string.save, new C01351()).setView(view).create().show();
    }
}
