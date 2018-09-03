package com.ansoft.speedup.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import com.ansoft.speedup.GovernorItem;
import com.ansoft.speedup.MainActivity;
import com.ansoft.speedup.R;
import com.ansoft.speedup.util.ClickEffect;
import com.ansoft.speedup.util.Cpufreq;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

public class GovernorFragment extends Fragment implements PageFragment {
    Activity activity;
    Context context;
    Cpufreq cpufreq;
    Editor editor;
    String governor;
    ArrayList<GovernorItem> governorItems;
    Handler handler;
    String[] params;
    Runnable refreshCounter;
    CheckBox setOnBoot;
    CheckBox setOnProfile;
    SharedPreferences settings;
    View view;
    ListView listView;

    /* renamed from: com.ansoft.setthecpu.fragment.GovernorFragment.1 */
    class C00871 implements Runnable {
        C00871() {
        }

        public void run() {
            if (!GovernorFragment.this.cpufreq.getGovernor().equals(GovernorFragment.this.governor)) {
                GovernorFragment.this.refresh();
                GovernorFragment.this.governor = GovernorFragment.this.cpufreq.getGovernor();
            }
            GovernorFragment.this.handler.postDelayed(this, 1000);
        }
    }

    private class Clicker implements OnClickListener {
        private Clicker() {
        }

        public void onClick(View v) {
            if (v == GovernorFragment.this.setOnBoot) {
                GovernorFragment.this.editor.putBoolean("advancedBoot", GovernorFragment.this.setOnBoot.isChecked());
                GovernorFragment.this.editor.commit();
            } else if (v == GovernorFragment.this.setOnProfile) {
                GovernorFragment.this.editor.putBoolean("governorSetOnProfile", GovernorFragment.this.setOnProfile.isChecked());
                GovernorFragment.this.editor.commit();
            }
        }
    }

    private class GovernorAdapter extends ArrayAdapter<GovernorItem> {
        private ArrayList<GovernorItem> items;

        class C00881 implements OnClickListener {
            final /* synthetic */ int val$position;

            C00881(int i) {
                this.val$position = i;
            }

            public void onClick(View v) {
                new GovernorDialog(GovernorAdapter.this.items.get(this.val$position)).show();
            }
        }

        public GovernorAdapter(Context context, int textViewResourceId, ArrayList<GovernorItem> items) {
            super(context, textViewResourceId, items);
            this.items = items;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            if (v == null) {
                v = ((LayoutInflater) GovernorFragment.this.activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.advanceditem, null);
            }
            v.setOnClickListener(new C00881(position));
            GovernorItem item = items.get(position);
            if (item != null) {
                TextView name = (TextView) v.findViewById(R.id.name);
                TextView text = (TextView) v.findViewById(R.id.text);
                TextView value = (TextView) v.findViewById(R.id.value);
                if (name != null) {
                    name.setText(allCaps(item.getReadableName()));
                }
                if (text != null) {
                    text.setText(item.getText());
                }
                if (value != null) {
                    value.setText(Long.toString(item.getCurrent()));
                }
            }
            return v;
        }
    }

    private class GovernorDialog {
        GovernorItem item;
        class C00891 implements DialogInterface.OnClickListener {
            final EditText val$voltageEdit;

            C00891(EditText editText) {
                this.val$voltageEdit = editText;
            }

            public void onClick(DialogInterface arg0, int arg1) {
                try {
                    GovernorFragment.this.cpufreq.setGovernorParam(GovernorDialog.this.item.getName(), Long.parseLong(this.val$voltageEdit.getText().toString()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                GovernorFragment.this.refresh();
                GovernorFragment.this.saveAll(GovernorFragment.this.governorItems);
            }
        }

        GovernorDialog(GovernorItem item) {
            this.item = item;
        }

        protected void show() {
            showGovernorDialog(GovernorFragment.this.getActivity());
        }

        private void showGovernorDialog(Context context) {
            View layout = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.governordialog, (ViewGroup) GovernorFragment.this.activity.findViewById(R.id.layout_root));
            final EditText voltageEdit = (EditText) layout.findViewById(R.id.voltageEdit);
            voltageEdit.setText(Long.toString(this.item.getCurrent()));
            Builder builder = new Builder(context);
            builder.setView(layout);
            final AlertDialog alertDialog = builder.create();
            alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

            TextView title=(TextView)layout.findViewById(R.id.GovernerTitle);
            title.setText(allCaps(item.getReadableName()));
            Button applyBtn=(Button)layout.findViewById(R.id.applyButton);
            applyBtn.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        GovernorFragment.this.cpufreq.setGovernorParam(GovernorDialog.this.item.getName(), Long.parseLong(voltageEdit.getText().toString()));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    GovernorFragment.this.refresh();
                    GovernorFragment.this.saveAll(GovernorFragment.this.governorItems);
                    alertDialog.dismiss();
                }
            });
            ClickEffect.Opacity(applyBtn);
            alertDialog.show();
        }
    }

    public String allCaps(String letter){
        String[] strArray = letter.split(" ");
        StringBuilder builder = new StringBuilder();
        for (String s : strArray) {
            String cap = s.substring(0, 1).toUpperCase() + s.substring(1);
            builder.append(cap + " ");
        }
        return builder.toString();
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        setHasOptionsMenu(true);
        this.activity = getActivity();
        this.view = inflater.inflate(R.layout.advancednew, container, false);
        this.settings = this.activity.getSharedPreferences("SpeedUp", 0);
        this.editor = this.settings.edit();
        this.setOnBoot = (CheckBox) this.view.findViewById(R.id.set_on_boot);
        this.setOnProfile = (CheckBox) this.view.findViewById(R.id.set_on_profile);
        this.setOnBoot.setChecked(this.settings.getBoolean("advancedBoot", false));
        this.setOnProfile.setChecked(this.settings.getBoolean("governorSetOnProfile", false));
        Clicker c = new Clicker();
        listView=(ListView)view.findViewById(R.id.governerListView);
        this.setOnBoot.setOnClickListener(c);
        this.setOnProfile.setOnClickListener(c);
        return this.view;
    }

    public void onPause() {
        super.onPause();
        this.handler.removeCallbacks(this.refreshCounter);
    }

    public void onResume() {
        super.onResume();
        refresh();
        this.governor = this.cpufreq.getGovernor();
        this.handler = new Handler();
        this.refreshCounter = new C00871();
        this.handler.post(this.refreshCounter);
    }

    public Integer getTitleRes() {
        return Integer.valueOf(R.string.tab_governor);
    }

    public Integer getMenuRes() {
        return null;
    }

    private void refresh() {
        this.cpufreq = ((MainActivity) this.activity).getCpufreq();
        this.governorItems = new ArrayList();
        this.governor = this.cpufreq.getGovernor();
        this.cpufreq.refreshGovernor();
        this.params = this.cpufreq.getGovernorParams();
        if (this.params == null || !this.cpufreq.hasParams()) {
            listView.setVisibility(View.GONE);
            return;
        }
        listView.setVisibility(View.VISIBLE);
        for (File file : this.cpufreq.getGovernorParamFiles()) {
            String name = file.getName();
            if (!(name.endsWith("_max") || name.endsWith("_min"))) {
                this.governorItems.add(new GovernorItem(name, "", this.cpufreq));
            }
        }
        listView.setAdapter(new GovernorAdapter(this.activity, R.layout.advanceditem, this.governorItems));
    }

    private void saveAll(ArrayList<GovernorItem> items) {
        File directory = new File(this.activity.getFilesDir().getAbsolutePath() + "/governor/");
        directory.mkdirs();
        String save = "";
        Iterator i$ = items.iterator();
        while (i$.hasNext()) {
            GovernorItem item = (GovernorItem) i$.next();
            save = save + item.getName() + " " + item.getCurrent() + "\n";
        }
        new File(directory + "/" + this.cpufreq.getGovernor()).delete();
        try {
            FileOutputStream fos = new FileOutputStream(directory + "/" + this.cpufreq.getGovernor(), true);
            fos.write(save.getBytes());
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void onDetach() {
        super.onDetach();
    }
}
