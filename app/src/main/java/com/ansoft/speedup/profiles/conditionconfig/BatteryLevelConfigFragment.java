package com.ansoft.speedup.profiles.conditionconfig;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.ansoft.speedup.Inequalities;
import com.ansoft.speedup.R;
import com.ansoft.speedup.profiles.condition.BatteryLevelState;
import com.ansoft.speedup.profiles.condition.Condition;
import com.ansoft.speedup.profiles.condition.ConditionPickerFragment.OnConditionAddListener;
import com.ansoft.speedup.widget.NumberSliderContainer;

import java.util.ArrayList;
import java.util.List;

public class BatteryLevelConfigFragment extends ConditionConfigFragment {
    public static int name;
    BatteryLevelState batteryLevelState;
    NumberSliderContainer slider;
    TextView title;
    View view;
    Inequalities selectedInequality=Inequalities.GREATERTHAN;

    String[] inequalityNames = new String[]{"=", "<=", ">=", "<", ">", "!="};


    public BatteryLevelConfigFragment(){}

    /* renamed from: com.ansoft.setthecpu.profiles.conditionconfig.BatteryLevelConfigFragment.1 */
    class C01311 implements OnClickListener {
        C01311() {
        }

        public void onClick(DialogInterface dialog, int whichButton) {
            BatteryLevelConfigFragment.this.onPositiveClick();
            BatteryLevelConfigFragment.this.saveCondition();
        }
    }

    static {
        name = R.string.condition_battery;
    }

    @SuppressLint("ValidFragment")
    public BatteryLevelConfigFragment(Condition parentCondition, Condition condition, View parentView, OnConditionAddListener listener) {
        super(parentCondition, condition, parentView, listener);
    }

    @SuppressLint("ValidFragment")
    public BatteryLevelConfigFragment(Condition condition, View view, OnConditionSaveListener listener) {
        super(condition, view, listener);
    }

    protected void onPositiveClick() {
        Bundle bundle = new Bundle();
        bundle.putInt("level", this.slider.getMax());
        bundle.putString("inequality", getInequality());
        this.condition.set(bundle);
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = getActivity().getLayoutInflater().inflate(R.layout.layout_frag_battery_level, null);
        this.slider = (NumberSliderContainer) view.findViewById(R.id.container);
        this.slider.init(100, 0, this.condition.get().getInt("level", 50), "%", "");
        this.title=(TextView)view.findViewById(R.id.titleeee);
        title.setText("Battery Level");
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
        builder.setPositiveButton(R.string.save, new C01311());
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



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }
}
