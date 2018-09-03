package com.ansoft.speedup.profiles;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.ansoft.speedup.AddProfileActivity;
import com.ansoft.speedup.Constants;
import com.ansoft.speedup.R;
import com.ansoft.speedup.profiles.action.Action;
import com.ansoft.speedup.profiles.action.CpuFrequencyAction;
import com.ansoft.speedup.profiles.action.CpuGovernorAction;
import com.ansoft.speedup.profiles.action.IoSchedulerAction;
import com.ansoft.speedup.profiles.action.NotificationAction;
import com.ansoft.speedup.util.Cpufreq;
import com.ansoft.speedup.util.Utils;
import com.ansoft.speedup.util.WheelView;
import com.ansoft.speedup.widget.FreqSliderContainer;
import com.github.paolorotolo.appintro.AppIntroFragment;
import com.github.paolorotolo.appintro.ISlideBackgroundColorHolder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ActionEditFragment extends Fragment implements ISlideBackgroundColorHolder {
    CheckBox actionIoScheduler;
    CheckBox actionNotification;
    CheckBox actionSetGovernor;
    CheckBox actionSpeedUp;
    List<Action> actions;
    AddProfileActivity activity;
    Integer[] freq;
    String[] freqText;
    String[] governors;
    String[] ioschedulers;
    RadioGroup notificationGroup;
    private String[] notificationNames;
    CheckBox notificationPersistent;
    SharedPreferences settings;
    Spinner spinnerGovernor;
    Spinner spinnerIoScheduler;
    View container;

    WheelView wheelViewMax;
    WheelView wheelViewMin;

    public ActionEditFragment() {
        this.notificationNames = new String[]{"normal", "battery", "charge", "failsafe", "call", "time"};
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        activity = (AddProfileActivity) getActivity();
        Cpufreq cpufreq = new Cpufreq(getActivity(), false);
        View view = inflater.inflate(R.layout.action_editor_setcpu, container, false);
        wheelViewMax=(WheelView)view.findViewById(R.id.wheelview);
        wheelViewMin=(WheelView)view.findViewById(R.id.wheelview1);
        this.actionSpeedUp = (CheckBox) view.findViewById(R.id.action_set_cpu);
        this.actionSetGovernor = (CheckBox) view.findViewById(R.id.action_set_governor);
        this.actionIoScheduler = (CheckBox) view.findViewById(R.id.action_set_io_scheduler);
        this.actionNotification = (CheckBox) view.findViewById(R.id.action_show_notification);
        this.notificationPersistent = (CheckBox) view.findViewById(R.id.notificationPersistent);
        this.notificationGroup = (RadioGroup) view.findViewById(R.id.notificationGroup);
        getFrequencies();
        this.spinnerGovernor = (Spinner) view.findViewById(R.id.governorSpinner);
        this.spinnerIoScheduler = (Spinner) view.findViewById(R.id.ioSchedulerSpinner);
        this.governors = cpufreq.getGovernors();
        this.ioschedulers = cpufreq.getIoSchedulers();
        ArrayAdapter<String> spinnerArrayAdapter;
        if (this.governors != null) {
            spinnerArrayAdapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, this.governors);
            spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            this.spinnerGovernor.setAdapter(spinnerArrayAdapter);
        } else {
            this.actionSetGovernor.setChecked(false);
            this.actionSetGovernor.setVisibility(View.GONE);
            this.spinnerGovernor.setVisibility(View.GONE);
        }
        if (this.ioschedulers != null) {
            spinnerArrayAdapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, this.ioschedulers);
            spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            this.spinnerIoScheduler.setAdapter(spinnerArrayAdapter);
        } else {
            this.actionIoScheduler.setChecked(false);
            this.actionIoScheduler.setVisibility(View.GONE);
            this.spinnerIoScheduler.setVisibility(View.GONE);
        }
        this.actions = this.activity.getActionList();
        if (this.actions.isEmpty()) {
            this.spinnerGovernor.setSelection(Utils.getIndex(cpufreq.getGovernor(), this.governors));
            this.spinnerIoScheduler.setSelection(Utils.getIndex(cpufreq.getIoScheduler(), this.ioschedulers));
        } else {
            for (Action a : this.actions) {
                Bundle bundle;
                if (a.getType().equals(CpuFrequencyAction.class)) {
                    this.actionSpeedUp.setChecked(true);
                    bundle = a.get();

                    wheelViewMax=(WheelView)view.findViewById(R.id.wheelview);
                    wheelViewMin=(WheelView)view.findViewById(R.id.wheelview1);
                    wheelViewMax.selectIndex(getIndexFromHz(bundle.getInt("maxKHz")));
                    wheelViewMin.selectIndex(getIndexFromHz(bundle.getInt("minKHz")));
                } else if (a.getType().equals(CpuGovernorAction.class)) {
                    if (this.governors != null) {
                        this.actionSetGovernor.setChecked(true);
                        try {
                            this.spinnerGovernor.setSelection(Utils.getIndex(a.get().getString("governor"), this.governors));
                        } catch (Exception e) {
                            this.spinnerGovernor.setSelection(Utils.getIndex(cpufreq.getGovernor(), this.governors));
                        }
                    }
                } else if (a.getType().equals(IoSchedulerAction.class)) {
                    if (this.ioschedulers != null) {
                        this.actionIoScheduler.setChecked(true);
                        try {
                            this.spinnerIoScheduler.setSelection(Utils.getIndex(a.get().getString("scheduler"), this.ioschedulers));
                        } catch (Exception e2) {
                            this.spinnerIoScheduler.setSelection(Utils.getIndex(cpufreq.getIoScheduler(), this.ioschedulers));
                        }
                    }
                } else if (a.getType().equals(NotificationAction.class)) {
                    this.actionNotification.setChecked(true);
                    bundle = a.get();
                    RadioButton index = (RadioButton) this.notificationGroup.getChildAt(Utils.getIndex(bundle.getString("icon"), this.notificationNames));
                    this.notificationPersistent.setChecked(bundle.getBoolean("persistent"));
                    this.notificationGroup.check(index.getId());
                }
            }
        }
        this.container=view.getRootView();
        List<String> items=new ArrayList<>();

        for (int i=0; i<freq.length; i++){
            items.add(String.valueOf(freq[i]/1000));
        }
        wheelViewMax.setItems(items);
        if (wheelViewMax==null) {
            wheelViewMax.selectIndex(freq.length - 1);
        }
        wheelViewMax.setAdditionCenterMark(" Max");

        wheelViewMax.setOnWheelItemSelectedListener(new WheelView.OnWheelItemSelectedListener() {
            @Override
            public void onWheelItemChanged(WheelView wheelView, int position) {
                int max=position;
                int min=wheelViewMin.getSelectedPosition();

                if (max<min){
                    wheelViewMin.selectIndex(position);
                }
                updateActions();
            }

            @Override
            public void onWheelItemSelected(WheelView wheelView, int position) {
                int max=position;
                int min=wheelViewMin.getSelectedPosition();

                if (max<min){
                    wheelViewMin.selectIndex(position);
                }
                updateActions();
            }
        });

        wheelViewMin.setOnWheelItemSelectedListener(new WheelView.OnWheelItemSelectedListener() {
            @Override
            public void onWheelItemChanged(WheelView wheelView, int position) {
                int max=wheelViewMax.getSelectedPosition();
                int min=position;

                if (min>max){
                    wheelViewMax.selectIndex(position);
                }
                updateActions();
            }

            @Override
            public void onWheelItemSelected(WheelView wheelView, int position) {
                int max=wheelViewMax.getSelectedPosition();
                int min=position;

                if (min>max){
                    wheelViewMax.selectIndex(position);
                }
                updateActions();
            }
        });

        wheelViewMin.setItems(items);

        if (wheelViewMin==null) {
            wheelViewMin.selectIndex(0);
        }
        wheelViewMin.setAdditionCenterMark(" Min");

        return view;
    }

    public int getIndexFromHz(int khz){
        int hh=0;
        for (int i=0; i<freq.length; i++){
            if (freq[i]==khz){
                hh=i;
            }
        }
        return hh;
    }

    public void updateActions(){
        this.actions.clear();
        Bundle bundle;
        if (this.actionSpeedUp.isChecked()) {
            bundle = new Bundle();
            bundle.putInt("maxKHz", freq[wheelViewMax.getSelectedPosition()]);
            bundle.putInt("minKHz", freq[wheelViewMin.getSelectedPosition()]);
            CpuFrequencyAction action = new CpuFrequencyAction();
            action.set(bundle);
            this.actions.add(action);
        }
        if (this.actionSetGovernor.isChecked()) {
            bundle = new Bundle();
            bundle.putString("governor", this.governors[this.spinnerGovernor.getSelectedItemPosition()]);
            CpuGovernorAction action2 = new CpuGovernorAction();
            action2.set(bundle);
            this.actions.add(action2);
        }
        if (this.actionIoScheduler.isChecked()) {
            bundle = new Bundle();
            bundle.putString("scheduler", this.ioschedulers[this.spinnerIoScheduler.getSelectedItemPosition()]);
            IoSchedulerAction action3 = new IoSchedulerAction();
            action3.set(bundle);
            this.actions.add(action3);
        }
        if (this.actionNotification.isChecked()) {
            bundle = new Bundle();
            bundle.putString("icon", this.notificationNames[this.notificationGroup.indexOfChild(this.notificationGroup.findViewById(this.notificationGroup.getCheckedRadioButtonId()))]);
            bundle.putBoolean("persistent", this.notificationPersistent.isChecked());
            NotificationAction action4 = new NotificationAction();
            action4.set(bundle);
            this.actions.add(action4);
        }
    }

    public void onPause() {

        super.onPause();
        this.actions.clear();
        Bundle bundle;
        if (this.actionSpeedUp.isChecked()) {
            bundle = new Bundle();
            bundle.putInt("maxKHz", freq[wheelViewMax.getSelectedPosition()]);
            bundle.putInt("minKHz", freq[wheelViewMin.getSelectedPosition()]);
            CpuFrequencyAction action = new CpuFrequencyAction();
            action.set(bundle);
            this.actions.add(action);
        }
        if (this.actionSetGovernor.isChecked()) {
            bundle = new Bundle();
            bundle.putString("governor", this.governors[this.spinnerGovernor.getSelectedItemPosition()]);
            CpuGovernorAction action2 = new CpuGovernorAction();
            action2.set(bundle);
            this.actions.add(action2);
        }
        if (this.actionIoScheduler.isChecked()) {
            bundle = new Bundle();
            bundle.putString("scheduler", this.ioschedulers[this.spinnerIoScheduler.getSelectedItemPosition()]);
            IoSchedulerAction action3 = new IoSchedulerAction();
            action3.set(bundle);
            this.actions.add(action3);
        }
        if (this.actionNotification.isChecked()) {
            bundle = new Bundle();
            bundle.putString("icon", this.notificationNames[this.notificationGroup.indexOfChild(this.notificationGroup.findViewById(this.notificationGroup.getCheckedRadioButtonId()))]);
            bundle.putBoolean("persistent", this.notificationPersistent.isChecked());
            NotificationAction action4 = new NotificationAction();
            action4.set(bundle);
            this.actions.add(action4);
        }
    }

    private void getFrequencies() {
        this.settings = getActivity().getSharedPreferences("SpeedUp", 0);
        String device = this.settings.getString("device", "autodetect");
        Constants constants = new Constants(device);
        String read;
        if (device.contains("custom")) {
            try {
                Log.d("SpeedUp", "Custom Config");
                read = Utils.readFile("/sdcard/SpeedUp.txt");
                if (read == null || read == "") {
                    read = Utils.readFile("/system/sd/SpeedUp.txt");
                }
                if (read == null || read == "") {
                    read = Utils.readFile("/system/SpeedUp");
                }
                if (read == null || read == "") {
                    read = Utils.readFile("/data/local/SpeedUp");
                }
                if (read == null || read == "") {
                    device = "";
                } else {
                    Log.d("SpeedUp", "Custom frequencies detected: " + read);
                    this.freqText = read.trim().split(",");
                    this.freq = Utils.convertStringArray(this.freqText);
                    Arrays.sort(this.freq);
                    this.freqText = Utils.convertIntArray(this.freq);
                }
            } catch (Exception e) {
                device = "";
            }
        } else if (device.contains("autodetect")) {
            try {
                read = Utils.readFile("/sys/devices/system/cpu/cpu0/cpufreq/scaling_available_frequencies");
                if (read == "" || read == null) {
                    for (int i = 0; i <= 20; i++) {
                        this.freqText = Utils.autodetect();
                        if (this.freqText != null) {
                            break;
                        }
                    }
                } else if (!(read == "" || read == null)) {
                    this.freqText = read.trim().split(" ");
                }
                if (this.freqText != null) {
                    this.freq = Utils.convertStringArray(this.freqText);
                    Arrays.sort(this.freq);
                    this.freqText = Utils.convertIntArray(this.freq);
                } else {
                    device = "";
                }
            } catch (Exception e2) {
                e2.printStackTrace();
                Log.d("SpeedUp", "Error in detecting frequency list: " + e2);
                device = "";
            }
        }
        if (!device.contains("custom") && !device.contains("autodetect")) {
            this.freq = constants.getFreq();
            this.freqText = constants.getFreqText();
        }
    }

    @Override
    public int getDefaultBackgroundColor() {
        return getResources().getColor(R.color.white);
    }

    @Override
    public void setBackgroundColor(@ColorInt int backgroundColor) {

        if (container != null) {
            container.setBackgroundColor(backgroundColor);
        }
    }


}
