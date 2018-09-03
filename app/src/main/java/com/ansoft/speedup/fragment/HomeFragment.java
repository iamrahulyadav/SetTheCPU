package com.ansoft.speedup.fragment;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.ansoft.speedup.Constants;
import com.ansoft.speedup.IntroActivity;
import com.ansoft.speedup.MainActivity;
import com.ansoft.speedup.PerflockActivity;
import com.ansoft.speedup.R;
import com.ansoft.speedup.circleprogress.ArcProgress;
import com.ansoft.speedup.profiles.ProfilesService;
import com.ansoft.speedup.util.Cpufreq;
import com.ansoft.speedup.util.Utils;
import com.ansoft.speedup.util.WheelView;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.joda.time.DateTimeConstants;

public class HomeFragment extends Fragment implements PageFragment {
    int NUM_CORES;
    Button about;
    private Activity activity;
    Button advanced;
    CheckBox boot;
    int[] bounds;
    Clicker clicker;
    Context context;
    Cpufreq cpufreq;
    Button donate;
    Editor editor;
    boolean enableSpinner;
    private Integer[] freq;
    private String[] freqText;
    private String[] governors;
    Handler handler;
    boolean hasRoot;
    DataInputStream in;
    Button info;
    ArcProgress status;
    DataOutputStream os;
    Process process;
    Button profiles;
    Button refresh;
    Runnable refreshCounter;
    private String[] schedulers;
    SharedPreferences settings;
    boolean stopRefresh;
    View view;

    WheelView wheelViewMax;
    WheelView wheelViewMin;

    FloatingActionButton mainFab, fab1, fab2, fab3;
    boolean isOpen=false;


    class C00945 implements Runnable {
        C00945() {
        }

        public void run() {
            HomeFragment.this.refresh();
            HomeFragment.this.handler.postDelayed(HomeFragment.this.refreshCounter, 500);
        }
    }



    class C00978 implements OnClickListener {
        C00978() {
        }

        public void onClick(DialogInterface arg0, int arg1) {
            InputStream ins = HomeFragment.this.getResources().openRawResource(R.raw.setcpu_safemode_toggle);
            try {
                byte[] buffer = new byte[ins.available()];
                ins.read(buffer);
                ins.close();
                FileOutputStream fos = new FileOutputStream(Environment.getExternalStorageDirectory() + "/" + "SpeedUp_safemode_toggle.zip");
                fos.write(buffer);
                fos.close();
                if (new File(Environment.getExternalStorageDirectory() + "/" + "SpeedUp_safemode_toggle.zip").exists()) {
                    return;
                }
                throw new IOException();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private class Clicker implements View.OnClickListener {
        private Clicker() {
        }

        public void onClick(View v) {
            if (v == HomeFragment.this.boot) {
                if (HomeFragment.this.settings.getInt("startBoot", 0) == 0) {
                    HomeFragment.this.editor.putInt("startBoot", 1);
                } else if (HomeFragment.this.settings.getInt("startBoot", 0) == 1) {
                    HomeFragment.this.editor.putInt("startBoot", 0);
                }
                HomeFragment.this.editor.commit();
            }
        }
    }


    public HomeFragment() {
        this.freqText = new String[]{""};
        this.governors = new String[]{""};
        this.clicker = new Clicker();
        this.hasRoot = true;
        this.stopRefresh = false;
        this.handler = new Handler();
        this.enableSpinner = false;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        setHasOptionsMenu(true);
        this.activity = getActivity();
        this.view = inflater.inflate(R.layout.home, container, false);
        wheelViewMax=(WheelView)view.findViewById(R.id.wheelview);
        wheelViewMin=(WheelView)view.findViewById(R.id.wheelview1);
        mainFab=(FloatingActionButton)view.findViewById(R.id.fab);
        fab1=(FloatingActionButton)view.findViewById(R.id.fab1);
        fab2=(FloatingActionButton)view.findViewById(R.id.fab2);
        fab3=(FloatingActionButton)view.findViewById(R.id.fab3);

        mainFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<FloatingActionButton> fabs=new ArrayList<FloatingActionButton>();
                fabs.add(fab1);
                fabs.add(fab2);
                fabs.add(fab3);
                isOpen= MainActivity.animateFAB(isOpen, mainFab, fabs);
            }
        });

        fab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editor.putInt("androidVersion", 2);
                editor.putBoolean("hasRoot", false);
                editor.putBoolean("showRootWarning", true);
                editor.commit();
                startActivity(new Intent(activity, IntroActivity.class));
                activity.finish();
            }
        });

        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(activity, PerflockActivity.class));
            }
        });
        fab3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showExtractDialog();
            }
        });
        this.context = this.activity.getApplicationContext();
        this.settings = this.activity.getSharedPreferences("SpeedUp", 0);
        this.editor = this.settings.edit();
        String device = this.settings.getString("device", "htc_msm");
        Constants constants = new Constants(device);
        this.hasRoot = ((MainActivity) this.activity).getHasRoot();
        this.cpufreq = ((MainActivity) this.activity).getCpufreq();
        this.NUM_CORES = this.cpufreq.getNumCores();
        if (this.settings.getBoolean("firstLaunch", true)) {
            int[] def = readMaxMinFrequency();
            this.editor.putInt("max", def[0]);
            this.editor.putInt("min", def[1]);
            this.editor.putString("stringGovernor", this.cpufreq.getGovernor());
            this.editor.commit();
        }
        new IntentFilter().addAction("SpeedUp.intent.action.profile");
        String read;
        if (device.contains("custom")) {
            try {
                Log.d("SpeedUp", "Custom Config");
                read = readInfo("/sdcard/SpeedUp.txt");
                if (read == null || read == "") {
                    read = readInfo("/system/sd/SpeedUp.txt");
                }
                if (read == null || read == "") {
                    read = readInfo("/system/SpeedUp");
                }
                if (read == null || read == "") {
                    read = readInfo("/data/local/SpeedUp");
                }
                if (read == null || read == "") {
                    device = "";
                } else {
                    Log.d("SpeedUp", "Custom frequencies detected: " + read);
                    this.freqText = read.trim().split(",");
                    this.freq = convertStringArray(this.freqText);
                    Arrays.sort(this.freq);
                    this.freqText = convertIntArray(this.freq);
                }
            } catch (Exception e) {
                device = "";
            }
        } else if (device.contains("autodetect")) {
            try {
                read = readInfo("/sys/devices/system/cpu/cpu0/cpufreq/scaling_available_frequencies");
                if (read == "" || read == null) {
                    for (int i = 0; i <= 20; i++) {
                        this.freqText = Utils.autodetect();
                        if (this.freqText != null) {
                            break;
                        }
                        Thread.sleep(50);
                    }
                } else if (!(read == "" || read == null)) {
                    this.freqText = read.split(" ");
                }
                if (this.freqText != null) {
                    this.freq = convertStringArray(this.freqText);
                    Arrays.sort(this.freq);
                    this.freqText = convertIntArray(this.freq);
                } else {
                    device = "";
                }
            } catch (Exception e2) {
                Log.d("SpeedUp", "Error in detecting frequency list: " + e2);
                device = "";
            }
        }
        if (!(device.contains("custom") || device.contains("autodetect"))) {
            this.freq = constants.getFreq();
            this.freqText = constants.getFreqText();
        }
        status=(ArcProgress) view.findViewById(R.id.arc_progress) ;

        if (HomeFragment.this.settings.getBoolean("governorSetOnProfile", false)) {
            HomeFragment.this.cpufreq.setGovernorFromSaved(HomeFragment.this.cpufreq.getGovernor(), HomeFragment.this.context.getFilesDir().getAbsolutePath());
        }
        this.boot = (CheckBox) this.view.findViewById(R.id.checkBoot);
        this.status.setProgress(readFrequency() / DateTimeConstants.MILLIS_PER_SECOND);
        status.setBottomText("MHz");
        Log.e("MAX",freq[freq.length-1]+"Mhz" );
        status.setMax(freq[freq.length-1]/1000);
        this.boot.setOnClickListener(this.clicker);
        this.bounds = readMaxMinFrequency();
        try {
            this.governors = this.cpufreq.getGovernors();
        } catch (NullPointerException e3) {
            e3.printStackTrace();
        }
        for (int i=0; i<governors.length; i++){

            Log.e("GOVERNERS", governors[i]);
        }
        this.schedulers = this.cpufreq.getIoSchedulers();

        if (this.settings.getInt("startBoot", 0) == 1) {
            this.boot.setChecked(true);
        }
        this.refreshCounter = new C00945();
        this.handler.post(this.refreshCounter);
        if (this.settings.getBoolean("profilesOn", false) && !isServiceRunning()) {
            Intent i2 = new Intent();
            i2.setClass(this.activity, ProfilesService.class);
            this.activity.startService(i2);
        }


        List<String> items=new ArrayList<>();

        for (int i=0; i<freq.length; i++){
            items.add(String.valueOf(freq[i]/1000));
        }
        updateLog();
        int minIndex=0;
        int maxIndex=0;

        int minPref=settings.getInt("min", this.freq[this.freq.length - 1].intValue());
        int maxPref=settings.getInt("max", this.freq[this.freq.length - 1].intValue());
        for (int i=0; i<freq.length; i++){
            if (minPref==freq[i]){
                minIndex=i;
            }
            if (maxPref==freq[i]){
                maxIndex=i;
            }
        }
        wheelViewMax.setItems(items);
        wheelViewMax.selectIndex(maxIndex);
        wheelViewMax.setAdditionCenterMark(" Max");

        wheelViewMax.setOnWheelItemSelectedListener(new WheelView.OnWheelItemSelectedListener() {
            @Override
            public void onWheelItemChanged(WheelView wheelView, int position) {
                int max=position;
                int min=wheelViewMin.getSelectedPosition();

                if (max<min){
                    wheelViewMin.selectIndex(position);
                }

                UpdateMaxMin();
                updateLog();
            }

            @Override
            public void onWheelItemSelected(WheelView wheelView, int position) {
                int max=position;
                int min=wheelViewMin.getSelectedPosition();

                if (max<min){
                    wheelViewMin.selectIndex(position);
                }
                UpdateMaxMin();
                updateLog();
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
                UpdateMaxMin();
                updateLog();
            }

            @Override
            public void onWheelItemSelected(WheelView wheelView, int position) {
                int max=wheelViewMax.getSelectedPosition();
                int min=position;

                if (min>max){
                    wheelViewMax.selectIndex(position);
                }
                UpdateMaxMin();
                updateLog();
            }
        });

        wheelViewMin.setItems(items);

        wheelViewMin.selectIndex(minIndex);
        wheelViewMin.setAdditionCenterMark(" Min");

        /*
        this.freqSliderContainer.init(this.freq, this.settings.getInt("max", this.freq[this.freq.length - 1].intValue()), this.settings.getInt("min", this.freq[0].intValue()));
        this.freqSliderContainer.setOnFreqChangeListener(new OnFreqChangeListener());
        if (!this.hasRoot) {
            this.boot.setEnabled(false);
            this.freqSliderContainer.setEnabled(false);
        }

        */


        HomeFragment.this.cpufreq.setGovernor("performance");
        HomeFragment.this.storeGovernor("performance");
        return this.view;
    }

    public void updateLog(){
        Log.e("MAX_PREF", cpufreq.getMax()+" Mhz");
        Log.e("MIN_PREF", cpufreq.getMin()+" Mhz");
    }

    private void UpdateMaxMin() {
        int maxHz=freq[wheelViewMax.getSelectedPosition()];
        int minHz=freq[wheelViewMin.getSelectedPosition()];
        editor.putInt("max", maxHz);
        editor.putInt("min", minHz);
        editor.commit();
        cpufreq.setFrequency(maxHz, minHz);
        handler.removeCallbacks(HomeFragment.this.refreshCounter);
        handler.postAtFrontOfQueue(HomeFragment.this.refreshCounter);
    }

    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putBoolean("hasRoot", this.hasRoot);
    }

    public Integer getTitleRes() {
        return Integer.valueOf(R.string.tab_main);
    }

    public Integer getMenuRes() {
        return Integer.valueOf(R.menu.main);
    }

    private int[] readMaxMinFrequency() {
        int[] out = new int[]{0, 0};
        try {
            out[0] = Integer.parseInt(readInfo("/sys/devices/system/cpu/cpu0/cpufreq/scaling_max_freq"));
            out[1] = Integer.parseInt(readInfo("/sys/devices/system/cpu/cpu0/cpufreq/scaling_min_freq"));
        } catch (NumberFormatException e) {
        }
        return out;
    }

    private int readFrequency() {
        String curFreq = readInfo("/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_cur_freq");
        for (int count = 0; count < 10; count++) {
            if (curFreq != null) {
                try {
                    if (curFreq != "") {
                        return Integer.parseInt(curFreq);
                    }
                } catch (NumberFormatException e) {
                }
            }
            curFreq = readInfo("/sys/devices/system/cpu/cpu0/cpufreq/scaling_cur_freq");
            if (curFreq != null && curFreq != "") {
                return Integer.parseInt(curFreq);
            }
            curFreq = readInfo("/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_cur_freq");
        }
        return 0;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_device_selection:
                this.editor.putInt("androidVersion", 2);
                this.editor.putBoolean("hasRoot", false);
                this.editor.putBoolean("showRootWarning", true);
                this.editor.commit();
                startActivity(new Intent(this.activity, IntroActivity.class));
                this.activity.finish();
                return true;
            case R.id.menu_perflock_disable:
                startActivity(new Intent(this.activity, PerflockActivity.class));
                return true;
            case R.id.menu_safemode:
                showExtractDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void storeGovernor(String governor) {
        this.editor.putString("stringGovernor", governor);
        this.editor.commit();
    }


    private int getIndex(String f, String[] array) {
        int i = 0;
        while (i < array.length) {
            if (array[i].equals(f) || f.equals(array[i])) {
                return i;
            }
            i++;
        }
        return 0;
    }

    public void refresh() {
        this.status.setProgress(readFrequency() / DateTimeConstants.MILLIS_PER_SECOND);
        status.setBottomText("MHz");
        this.bounds = readMaxMinFrequency();
    }

    private String readInfo(String filename) {
        this.in = null;
        String lines = "";
        try {
            this.in = new DataInputStream(new FileInputStream(filename));
            while (true) {
                String line = this.in.readLine();
                if (line == null) {
                    break;
                }
                lines = lines + line.trim() + "\n";
            }
            return lines.trim();
        } catch (Exception e) {
            return null;
        } finally {
            try {
                this.in.close();
            } catch (Exception e2) {
                return null;
            }
        }
    }

    private Integer[] convertStringArray(String[] sarray) {
        if (sarray == null) {
            return null;
        }
        Integer[] numArr = new Integer[sarray.length];
        for (int i = 0; i < sarray.length; i++) {
            numArr[i] = Integer.valueOf(Integer.parseInt(sarray[i].trim()));
        }
        return numArr;
    }

    private String[] convertIntArray(Integer[] sarray) {
        if (sarray == null) {
            return null;
        }
        String[] strArr = new String[sarray.length];
        for (int i = 0; i < sarray.length; i++) {
            strArr[i] = "" + sarray[i];
        }
        return strArr;
    }

    private void showExtractDialog() {
        View layout = ((LayoutInflater) this.activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.root_dialog, (ViewGroup) this.activity.findViewById(R.id.layout_root));

        TextView textView=(TextView)layout.findViewById(R.id.titleeee);
        textView.setText(getResources().getString(R.string.safemode_title));

        TextView descTv=(TextView)layout.findViewById(R.id.descriptionDD);
        descTv.setText(getResources().getString(R.string.safemode_warn));

        Builder builder=new Builder(this.activity);
        builder.setPositiveButton(getResources().getString(R.string.extract), new C00978());
        AlertDialog dialog=builder.create();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setView(layout);
        dialog.show();
    }

    public void onPause() {
        super.onPause();
        this.handler.removeCallbacks(this.refreshCounter);
        if (this.settings.getBoolean("profilesOn", false) && !isServiceRunning()) {
            Intent intent = new Intent();
            intent.setClass(this.activity, ProfilesService.class);
            this.activity.startService(intent);
        }
    }

    public void onResume() {
        super.onResume();
        this.handler.removeCallbacks(this.refreshCounter);
        this.handler.post(this.refreshCounter);
    }

    public void onDetach() {
        super.onDetach();
        this.handler.removeCallbacks(this.refreshCounter);
    }

    private boolean isServiceRunning() {
        for (RunningServiceInfo service : ((ActivityManager) this.activity.getSystemService(Context.ACTIVITY_SERVICE)).getRunningServices(Integer.MAX_VALUE)) {
            if ("com.ansoft.afterroot.profiles.ProfilesService".equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}
