package com.ansoft.speedup.profiles.conditionconfig;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.ansoft.speedup.R;
import com.ansoft.speedup.profiles.condition.Condition;
import com.ansoft.speedup.profiles.condition.ConditionPickerFragment.OnConditionAddListener;

public class ScreenStateConfigFragment extends ConditionConfigFragment {
    public static int name;

    public ScreenStateConfigFragment(){}

    /* renamed from: com.ansoft.setthecpu.profiles.conditionconfig.ScreenStateConfigFragment.1 */
    class C01371 implements OnClickListener {
        C01371() {
        }

        public void onClick(DialogInterface dialog, int whichButton) {
            ScreenStateConfigFragment.this.onPositiveClick();
            ScreenStateConfigFragment.this.saveCondition();
        }
    }

    static {
        name = R.string.condition_screen;
    }

    @SuppressLint("ValidFragment")
    public ScreenStateConfigFragment(Condition parentCondition, Condition condition, View parentView, OnConditionAddListener listener) {
        super(parentCondition, condition, parentView, listener);
    }

    @SuppressLint("ValidFragment")
    public ScreenStateConfigFragment(Condition condition, View view, OnConditionSaveListener listener) {
        super(condition, view, listener);
    }
    protected void onPositiveClick(boolean tt) {
        Bundle bundle = new Bundle();
        bundle.putBoolean("inCall", tt);
        this.condition.set(bundle);

    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = getActivity().getLayoutInflater().inflate(R.layout.layout_on_off_frag, null);
        TextView title=(TextView)view.findViewById(R.id.titleeee);
        title.setText(this.condition.getName(getActivity()));

        Button onBtn=(Button)view.findViewById(R.id.onBtn);
        Button offBtn=(Button)view.findViewById(R.id.offBtn);

        onBtn.setText("Screen On");
        offBtn.setText("Screen Off");

        Builder builder=new Builder(getActivity());
        final AlertDialog dialog=builder.create();
        onBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onPositiveClick(true);
                saveCondition();
                dialog.dismiss();
            }
        });
        offBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onPositiveClick(false);
                saveCondition();
                dialog.dismiss();
            }
        });
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setView(view);
        dialog.show();
    }
}
