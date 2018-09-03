package com.ansoft.speedup.profiles.conditionconfig;

import android.annotation.SuppressLint;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.ToggleButton;

import com.ansoft.speedup.R;
import com.ansoft.speedup.profiles.condition.Condition;
import com.ansoft.speedup.profiles.condition.ConditionPickerFragment.OnConditionAddListener;
import com.ansoft.speedup.profiles.condition.ScreenState;

public class AppConfigFragment extends ConditionConfigFragment {
    ToggleButton button;
    Intent intent;
    ProgressDialog loadingDialog;
    Intent pickIntent;
    ScreenState screenState;
    View view;

    public  AppConfigFragment(){}

    private class CustomListener implements OnClickListener {
        private CustomListener() {
        }

        public void onClick(DialogInterface dialog, int which) {
            if (which == 0) {
                AppConfigFragment.this.intent = new Intent("android.intent.action.MAIN", null);
                AppConfigFragment.this.intent.addCategory("android.intent.category.LAUNCHER");
                AppConfigFragment.this.pickIntent = new Intent("android.intent.action.PICK_ACTIVITY");
                AppConfigFragment.this.pickIntent.putExtra("android.intent.extra.INTENT", AppConfigFragment.this.intent);
                AppConfigFragment.this.startDialog();
            } else if (which == 1) {
                AppConfigFragment.this.intent = new Intent("android.intent.action.MAIN", null);
                AppConfigFragment.this.intent.addCategory("android.intent.category.HOME");
                AppConfigFragment.this.pickIntent = new Intent("android.intent.action.PICK_ACTIVITY");
                AppConfigFragment.this.pickIntent.putExtra("android.intent.extra.INTENT", AppConfigFragment.this.intent);
                AppConfigFragment.this.startDialog();
            }
        }
    }

    @SuppressLint("ValidFragment")
    public AppConfigFragment(Condition parentCondition, Condition condition, View parentView, OnConditionAddListener listener) {
        super(parentCondition, condition, parentView, listener);
    }

    @SuppressLint("ValidFragment")
    public AppConfigFragment(Condition condition, View view, OnConditionSaveListener listener) {
        super(condition, view, listener);
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.loadingDialog = new ProgressDialog(getActivity());
        this.loadingDialog.setTitle(this.condition.getName(getActivity()));
        this.loadingDialog.setMessage(getActivity().getString(R.string.condition_app_loading));
        String[] items = new String[]{getActivity().getString(R.string.condition_app_type_normal), getActivity().getString(R.string.condition_app_type_launcher)};
        Builder builder = new Builder(getActivity());
        builder.setTitle(getActivity().getString(R.string.condition_app_type));
        builder.setItems(items, new CustomListener());
        builder.create().show();
    }

    public void startDialog() {
        this.loadingDialog.show();
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
        if (this.pickIntent != null) {
            startActivityForResult(this.pickIntent, 0);
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
        if (this.loadingDialog != null && this.loadingDialog.isShowing()) {
            try {
                this.loadingDialog.dismiss();
            } catch (RuntimeException e) {
            }
        }
        if (requestCode == 0 && data != null) {
            PackageManager pm = getActivity().getPackageManager();
            for (ResolveInfo res : pm.queryIntentActivities(data, 0)) {
                Bundle bundle = new Bundle();
                bundle.putBoolean("matchPackageOnly", true);
                bundle.putString("activity", ComponentName.unflattenFromString(res.activityInfo.packageName + "/" + res.activityInfo.name).flattenToShortString());
                bundle.putString("activityName", (String) res.activityInfo.loadLabel(pm));
                this.condition.set(bundle);
                saveCondition();
            }
        }
    }
}
