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

import com.ansoft.speedup.R;
import com.ansoft.speedup.profiles.condition.Condition;
import com.ansoft.speedup.profiles.condition.ConditionPickerFragment.OnConditionAddListener;

public class ChargeConfigFragment extends ConditionConfigFragment {
    public static int name;
    TextView title;
    View view;
    String selectedState="Any";
    String [] names = new String[]{"Any", "AC", "USB", "wireless"};
    public ChargeConfigFragment(){}

    /* renamed from: com.ansoft.setthecpu.profiles.conditionconfig.ChargeConfigFragment.1 */
    class C01341 implements OnClickListener {
        C01341() {
        }

        public void onClick(DialogInterface dialog, int whichButton) {
            ChargeConfigFragment.this.onPositiveClick();
            ChargeConfigFragment.this.saveCondition();
        }
    }

    static {
        name = R.string.condition_charge;
    }

    @SuppressLint("ValidFragment")
    public ChargeConfigFragment(Condition parentCondition, Condition condition, View parentView, OnConditionAddListener listener) {
        super(parentCondition, condition, parentView, listener);
    }

    @SuppressLint("ValidFragment")
    public ChargeConfigFragment(Condition condition, View view, OnConditionSaveListener listener) {
        super(condition, view, listener);
    }

    protected void onPositiveClick() {
        Bundle bundle = new Bundle();
        bundle.putString("mode", selectedState);
        this.condition.set(bundle);
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = getActivity().getLayoutInflater().inflate(R.layout.layout_frag_charging_state, null);
        title=(TextView)view.findViewById(R.id.titleeee);
        title.setText(this.condition.getName(getActivity()));




        Spinner spinner=(Spinner)view.findViewById(R.id.spinner);
        ArrayAdapter adapter=new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, names);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedState=names[i];
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        Builder builder=new Builder(getActivity());
        builder.setPositiveButton(R.string.save, new C01341());
        AlertDialog dialog=builder.create();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setView(view);
        dialog.show();
    }
}
