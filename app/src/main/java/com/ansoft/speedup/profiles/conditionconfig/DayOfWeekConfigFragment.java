package com.ansoft.speedup.profiles.conditionconfig;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

import com.ansoft.speedup.R;
import com.ansoft.speedup.profiles.condition.Condition;
import com.ansoft.speedup.profiles.condition.ConditionPickerFragment.OnConditionAddListener;
import com.ansoft.speedup.util.SmoothCheckBox;

public class DayOfWeekConfigFragment extends ConditionConfigFragment {
    public static int name;
    Condition condition;
    SmoothCheckBox fri;
    SmoothCheckBox mon;
    SmoothCheckBox sat;
    SmoothCheckBox sun;
    SmoothCheckBox thu;
    SmoothCheckBox tue;
    View view;
    SmoothCheckBox wed;

    public DayOfWeekConfigFragment(){}

    class C01361 implements OnClickListener {
        C01361() {
        }

        public void onClick(DialogInterface dialog, int whichButton) {
            DayOfWeekConfigFragment.this.onPositiveClick();
            DayOfWeekConfigFragment.this.saveCondition();
        }
    }

    static {
        name = R.string.condition_dayofweek;
    }

    @SuppressLint("ValidFragment")
    public DayOfWeekConfigFragment(Condition parentCondition, Condition condition, View parentView, OnConditionAddListener listener) {
        super(parentCondition, condition, parentView, listener);
        this.condition = condition;
    }

    @SuppressLint("ValidFragment")
    public DayOfWeekConfigFragment(Condition condition, View view, OnConditionSaveListener listener) {
        super(condition, view, listener);
        this.condition = condition;
    }

    protected void onPositiveClick() {
        Bundle bundle = new Bundle();
        bundle.putBooleanArray("days", new boolean[]{this.mon.isChecked(), this.tue.isChecked(), this.wed.isChecked(), this.thu.isChecked(), this.fri.isChecked(), this.sat.isChecked(), this.sun.isChecked()});
        this.condition.set(bundle);
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = getActivity().getLayoutInflater().inflate(R.layout.dayofweek_config_dialog, null);
        this.mon = (SmoothCheckBox) view.findViewById(R.id.mon);
        this.tue = (SmoothCheckBox) view.findViewById(R.id.tue);
        this.wed = (SmoothCheckBox) view.findViewById(R.id.wed);
        this.thu = (SmoothCheckBox) view.findViewById(R.id.thu);
        this.fri = (SmoothCheckBox) view.findViewById(R.id.fri);
        this.sat = (SmoothCheckBox) view.findViewById(R.id.sat);
        this.sun = (SmoothCheckBox) view.findViewById(R.id.sun);
        if (this.condition.get().containsKey("days")) {
            boolean[] values = this.condition.get().getBooleanArray("days");
            this.mon.setChecked(values[0], true);
            this.tue.setChecked(values[1], true);
            this.wed.setChecked(values[2], true);
            this.thu.setChecked(values[3], true);
            this.fri.setChecked(values[4], true);
            this.sat.setChecked(values[5], true);
            this.sun.setChecked(values[6], true);
        }

        Builder builder=new Builder(getActivity());
        builder.setPositiveButton(R.string.save, new C01361());
        AlertDialog dialog=builder.create();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setView(view);
        dialog.show();

    }
}
