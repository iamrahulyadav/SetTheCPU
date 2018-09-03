package com.ansoft.speedup.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.ansoft.speedup.R;

public class HintAlertDialog {

    Activity activity;
    AlertDialog.Builder builders;
    String title;
    String summary;

    public HintAlertDialog(Activity activity, String title, String summary) {
        this.activity = activity;
        this.title=title;
        this.summary=summary;
    }

    public void show() {
        builders = new AlertDialog.Builder(activity);

        builders.setPositiveButton("OK", null);
        LayoutInflater inflater = activity.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.alert_thanks_for_reg,
                null);
        builders.setView(dialogView);
        final AlertDialog builder=builders.create();
        builder.setCanceledOnTouchOutside(true);
        builder.setCancelable(true);
        TextView title=(TextView)dialogView.findViewById(R.id.hintTitle);
        title.setText(this.title);
        TextView summary=(TextView)dialogView.findViewById(R.id.blahTxt);
        summary.setText(this.summary);

        builder.show();
    }
}
