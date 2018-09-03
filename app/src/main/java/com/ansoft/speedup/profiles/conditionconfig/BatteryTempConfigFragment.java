package com.ansoft.speedup.profiles.conditionconfig;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.ansoft.speedup.Inequalities;
import com.ansoft.speedup.R;
import com.ansoft.speedup.profiles.condition.BatteryTempState;
import com.ansoft.speedup.profiles.condition.Condition;
import com.ansoft.speedup.profiles.condition.ConditionPickerFragment.OnConditionAddListener;
import com.ansoft.speedup.widget.NumberSliderContainer;

import java.util.ArrayList;
import java.util.List;

public class BatteryTempConfigFragment extends ConditionConfigFragment {
    public static int name;
    BatteryTempState batteryTempState;
    NumberSliderContainer slider;
    TextView title;
    View view;
    Inequalities selectedInequality=Inequalities.GREATERTHAN;

    String[] inequalityNames = new String[]{"=", "<=", ">=", "<", ">", "!="};

    public BatteryTempConfigFragment(){}

    /* renamed from: com.ansoft.setthecpu.profiles.conditionconfig.BatteryTempConfigFragment.1 */
    class C01321 implements OnClickListener {
        C01321() {
        }

        public void onClick(DialogInterface dialog, int whichButton) {
            BatteryTempConfigFragment.this.onPositiveClick();
            BatteryTempConfigFragment.this.saveCondition();
        }
    }

    static {
        name = R.string.condition_battery_temp;
    }

    @SuppressLint("ValidFragment")
    public BatteryTempConfigFragment(Condition parentCondition, Condition condition, View parentView, OnConditionAddListener listener) {
        super(parentCondition, condition, parentView, listener);
    }

    @SuppressLint("ValidFragment")
    public BatteryTempConfigFragment(Condition condition, View view, OnConditionSaveListener listener) {
        super(condition, view, listener);
    }

    protected void onPositiveClick() {
        Bundle bundle = new Bundle();
        bundle.putInt("temp", this.slider.getMax() * 10);
        bundle.putString("inequality", getInequality());
        this.condition.set(bundle);
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = getActivity().getLayoutInflater().inflate(R.layout.layout_frag_battery_level, null);
        this.slider = (NumberSliderContainer) view.findViewById(R.id.container);
        this.slider.init(100, 0, this.condition.get().getInt("temp", 500) / 10, " \u00b0C", "");




        this.title=(TextView)view.findViewById(R.id.titleeee);
        title.setText(condition.getName(getActivity()));
        final List<Inequalities> ineqs=new ArrayList<>();
        ineqs.add(Inequalities.GREATERTHANEQUAL);
        ineqs.add(Inequalities.LESSTHANEQUAL);
        ineqs.add(Inequalities.GREATERTHAN);
        ineqs.add(Inequalities.NOTEQUALS);
        ineqs.add(Inequalities.LESSTHAN);
        ineqs.add(Inequalities.EQUALS);

        final List<String> ineqString=new ArrayList<>();
        ineqString.add("Greater Than or Equals");
        ineqString.add("Less Than or Equals");
        ineqString.add("Greater Than");
        ineqString.add("Does not equals");
        ineqString.add("Less Than");
        ineqString.add("Equals");


        Spinner spinner=(Spinner)view.findViewById(R.id.spinner);
        ArrayAdapter adapter=new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, ineqString);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedInequality=ineqs.get(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });



        Builder builder=new Builder(getActivity());
        builder.setPositiveButton(R.string.save, new C01321());
        AlertDialog dialog=builder.create();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setView(view);
        dialog.show();




    }

    public String getInequality(){
        switch (selectedInequality){
            case GREATERTHANEQUAL:
                return inequalityNames[2];
            case LESSTHANEQUAL:
                return inequalityNames[1];
            case GREATERTHAN:
                return inequalityNames[4];
            case LESSTHAN:
                return inequalityNames[3];
            case NOTEQUALS:
                return inequalityNames[5];
            case EQUALS:
                return inequalityNames[0];
        }
        return inequalityNames[2];
    }
}
