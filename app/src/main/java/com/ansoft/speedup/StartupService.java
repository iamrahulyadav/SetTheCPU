package com.ansoft.speedup;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.IBinder;
import android.util.Log;
import com.ansoft.speedup.profiles.ProfilesService;
import com.ansoft.speedup.util.Cpufreq;
import com.ansoft.speedup.util.Utils;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;

public class StartupService extends Service {
    String GOVERNOR_DIR;
    final String GOVERNOR_DIR_NEW;
    final String GOVERNOR_DIR_OLD;
    int NUM_CORES;
    Cpufreq cpufreq;
    String currentGovernor;
    int defaultMax;
    int defaultMin;
    Integer[] freq;
    String[] freqText;
    Process process;
    SharedPreferences settings;

    private class VoltageHelper {
        final String CPUFREQ_DIR;
        String UV_mV_table;
        public Integer[] enabled;
        public Integer[] frequencies;
        String frequency_voltage_table;
        boolean g2x;
        public int length;
        public Integer[] max;
        boolean nexusS;
        File saveFile;
        boolean statesEnabled;
        String states_enabled_table;
        public Integer[] voltage;
        public Integer[] voltageDifference;

        VoltageHelper() {
            this.CPUFREQ_DIR = Cpufreq.GOVERNOR_DIR_OLD;
            this.UV_mV_table = "UV_mV_table";
            this.frequency_voltage_table = "frequency_voltage_table";
            this.states_enabled_table = "states_enabled_table";
            this.g2x = false;
            this.nexusS = false;
            this.statesEnabled = false;
            File dir = StartupService.this.getApplicationContext().getDir("voltage", 0);
            this.saveFile = new File(dir.getAbsolutePath() + "/voltages");
            if (new File("/sys/devices/system/cpu/cpu0/cpufreq/FakeShmoo_UV_mV_table").exists()) {
                this.UV_mV_table = "FakeShmoo_" + this.UV_mV_table;
                this.frequency_voltage_table = "FakeShmoo_freq_voltage_table";
                this.states_enabled_table = "FakeShmoo_" + this.states_enabled_table;
                this.g2x = true;
            } else if (new File(Cpufreq.GOVERNOR_DIR_OLD + this.frequency_voltage_table).exists()) {
                this.g2x = true;
                this.saveFile = new File(dir.getAbsolutePath() + "/voltages_difference");
            } else if (new File(Cpufreq.GOVERNOR_DIR_OLD + this.UV_mV_table).exists() && Utils.readFile(Cpufreq.GOVERNOR_DIR_OLD + this.UV_mV_table).contains("mhz")) {
                this.nexusS = true;
                this.saveFile = new File(dir.getAbsolutePath() + "/voltages_absolute");
            } else {
                this.saveFile = new File(dir.getAbsolutePath() + "/voltages_difference");
            }
            if (new File(Cpufreq.GOVERNOR_DIR_OLD + this.states_enabled_table).exists()) {
                this.statesEnabled = true;
            }
            Log.d("SpeedUp", this.saveFile.getAbsolutePath());
            refresh();
        }

        private boolean getReverseOrder(String path, String split, int index) {
            Integer[] table = tableParser(path, split, index);
            if (table[table.length - 1].intValue() > table[0].intValue()) {
                return false;
            }
            return true;
        }

        public void refresh() {
            Log.d("SpeedUp", "refresh()");
            this.frequencies = StartupService.this.freq;
            if (!getReverseOrder(this.saveFile.getAbsolutePath(), "\n", 0)) {
                Log.d("SpeedUp", "sorting");
                Arrays.sort(this.frequencies);
            }
            this.length = StartupService.this.freq.length;
            if (this.saveFile.exists()) {
                Log.d("SpeedUp", "saveFile.exists()");
                if (Arrays.equals(tableParser(this.saveFile.getAbsolutePath(), "\n", 0), this.frequencies)) {
                    Log.d("SpeedUp", "Tables equal");
                    if (this.nexusS) {
                        this.voltage = tableParser(this.saveFile.getAbsolutePath(), "\n", 1);
                    } else {
                        Log.d("SpeedUp", "g2x");
                        this.max = tableParser(Cpufreq.GOVERNOR_DIR_OLD + this.frequency_voltage_table, "\n", 1);
                        this.voltageDifference = tableParser(this.saveFile.getAbsolutePath(), "\n", 1);
                    }
                    if (this.statesEnabled) {
                        Log.d("SpeedUp", "statesEnabled");
                        this.enabled = tableParser(this.saveFile.getAbsolutePath(), "\n", 2);
                        if (this.enabled == null) {
                        }
                    }
                    Log.d("SpeedUp", "applySettings()");
                    applySettings();
                    return;
                }
                Log.d("SpeedUp", "Kernel changed: Frequencies not the same as those saved in the table.");
            }
        }

        private Integer[] tableParser(String table, String split, int index) {
            try {
                String[] read = Utils.readFile(table).trim().split(split);
                Integer[] numArr = new Integer[read.length];
                for (int i = 0; i < read.length; i++) {
                    numArr[i] = Integer.valueOf(Integer.parseInt(read[i].split(" ")[index].replaceAll("[^0-9.]", "").trim()));
                }
                return numArr;
            } catch (NumberFormatException e) {
                e.printStackTrace();
                return null;
            } catch (Exception e2) {
                e2.printStackTrace();
                return null;
            }
        }

        private void applySettings() {
            Integer[] voltageTable;
            int i;
            String write = "";
            if (this.nexusS) {
                voltageTable = this.voltage;
            } else {
                voltageTable = this.voltageDifference;
            }
            for (i = 0; i < this.frequencies.length; i++) {
                write = write + voltageTable[i] + " ";
            }
            StartupService.this.cpufreq.runCommand("echo '" + write.trim() + "' > " + Cpufreq.GOVERNOR_DIR_OLD + this.UV_mV_table);
            Log.d("SpeedUp", "echo '" + write.trim() + "' > " + Cpufreq.GOVERNOR_DIR_OLD + this.UV_mV_table);
            if (this.statesEnabled) {
                String statesWrite = "";
                for (i = 0; i < this.frequencies.length; i++) {
                    statesWrite = statesWrite + this.enabled[i] + " ";
                }
                StartupService.this.cpufreq.runCommand("echo '" + statesWrite.trim() + "' > " + Cpufreq.GOVERNOR_DIR_OLD + this.states_enabled_table);
                Log.d("SpeedUp", "echo '" + statesWrite.trim() + "' > " + Cpufreq.GOVERNOR_DIR_OLD + this.states_enabled_table);
            }
        }
    }

    public StartupService() {
        this.GOVERNOR_DIR_NEW = Cpufreq.GOVERNOR_DIR_NEW;
        this.GOVERNOR_DIR_OLD = Cpufreq.GOVERNOR_DIR_OLD;
        this.GOVERNOR_DIR = Cpufreq.GOVERNOR_DIR_NEW;
    }

    public IBinder onBind(Intent arg0) {
        return null;
    }

    public void onCreate() {
        super.onCreate();
        File file = new File("/sdcard/SpeedUp_safemode");
        File safemode_data = new File("/data/SpeedUp_safemode");
        File safemode_data_local = new File("/data/local/SpeedUp_safemode");
        file = new File("/sdcard/external_sd/SpeedUp_safemode");
        File safemode2_sd = new File("/sdcard/.nocpu");
        File safemode2_data = new File("/data/.nocpu");
        File safemode2_data_local = new File("/data/local/.nocpu");
        File safemode2_ext_sd = new File("/sdcard/external_sd/.nocpu");
        this.settings = getSharedPreferences("SpeedUp", 0);
        Editor editor = this.settings.edit();
        if (!(file.exists() || safemode_data_local.exists() || file.exists() || safemode2_sd.exists() || safemode2_data.exists() || safemode2_ext_sd.exists() || safemode_data.exists() || safemode2_data_local.exists())) {
            if (!this.settings.getBoolean("safeModeBoot", false)) {
                editor.putBoolean("safeModeBoot", true);
                editor.commit();
                this.NUM_CORES = this.settings.getInt("cores", 1);
                getFrequencies();
                this.cpufreq = new Cpufreq(this, true);
                String scheduler = this.settings.getString("scheduler", "");
                if (scheduler != "") {
                    this.cpufreq.setIoScheduler(scheduler);
                }
                String governor = this.settings.getString("stringGovernor", "ondemand");
                boolean advancedBoot = this.settings.getBoolean("advancedBoot", false);
                boolean voltageBoot = this.settings.getBoolean("voltageSetBoot", false);
                int startBoot = this.settings.getInt("startBoot", 0);
                if (advancedBoot || startBoot == 1 || voltageBoot) {
                    this.cpufreq.fixPermissions();
                }
                if (startBoot == 1) {
                    int f = this.settings.getInt("max", this.defaultMax);
                    int g = this.settings.getInt("min", this.defaultMin);
                    this.cpufreq.setFrequency(f, g);
                    this.cpufreq.setGovernor(governor);
                }
                this.currentGovernor = readGovernor().trim();
                if (advancedBoot) {
                    this.cpufreq.setGovernorFromSaved(this.cpufreq.getGovernor(), getApplicationContext().getFilesDir().getAbsolutePath());
                }
                if (this.settings.getBoolean("profilesOn", false)) {
                    startService(new Intent(this, ProfilesService.class));
                }
                if (this.settings.getBoolean("perflockBoot", false)) {
                    Log.d("SpeedUp", "Calling perflock disabler on startup");
                    new PerflockDisable().doDisable(getApplicationContext(), getResources());
                }
                if (voltageBoot) {
                    Log.d("SpeedUp", "Applying saved voltages on startup");
                    VoltageHelper voltageHelper = new VoltageHelper();
                }
                this.cpufreq.kill();
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                editor.putBoolean("safeModeBoot", false);
                editor.commit();
                stopSelf();
            }
        }
        editor.putBoolean("safeModeBoot", false);
        editor.commit();
        Log.w("SpeedUp", "Last startup failed or failsafe file detected. Ignoring startup settings.");
        stopSelf();
    }

    private String readInfo(String filename) {
        Throwable th;
        InputStream ins = null;
        DataInputStream in = null;
        String lines = "";
        try {
            InputStream ins2 = new FileInputStream(new File(filename));
            try {
                DataInputStream in2 = new DataInputStream(ins2);
                while (true) {
                    try {
                        String line = in2.readLine();
                        if (line != null) {
                            lines = lines + line.trim() + "\n";
                        } else {
                            try {
                                ins2.close();
                                in2.close();
                                in = in2;
                                ins = ins2;
                                return lines.trim();
                            } catch (Exception e) {
                                in = in2;
                                ins = ins2;
                                return null;
                            }
                        }
                    } catch (Exception e2) {
                        in = in2;
                        ins = ins2;
                    } catch (Throwable th2) {
                        th = th2;
                        in = in2;
                        ins = ins2;
                    }
                }
            } catch (Exception e3) {
                ins = ins2;
                try {
                    ins.close();
                    in.close();
                    return null;
                } catch (Exception e4) {
                    return null;
                }
            } catch (Throwable th3) {
                th = th3;
                ins = ins2;
                try {
                    ins.close();
                    in.close();
                    throw th;
                } catch (Exception e5) {
                    return null;
                }
            }
        } catch (Exception e6) {
            try {
                ins.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        } catch (Throwable th4) {
            th = th4;
            try {
                ins.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                throw th;
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        }
        return lines;
    }

    private String readGovernorAttempt() {
        return readInfo("/sys/devices/system/cpu/cpu0/cpufreq/scaling_governor");
    }

    private String readGovernor() {
        String i = readGovernorAttempt();
        int c = 0;
        while (true) {
            if (i != null && i != "") {
                return i.trim();
            }
            i = readGovernorAttempt();
            if (c >= 10) {
                return "driver";
            }
            c++;
        }
    }

    private void getFrequencies() {
        String device = this.settings.getString("device", "htc_msm");
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
                    return;
                }
                Log.d("SpeedUp", "Custom frequencies detected: " + read);
                this.freqText = read.trim().split(",");
                this.freq = Utils.convertStringArrayToInteger(this.freqText);
                Arrays.sort(this.freq, Collections.reverseOrder());
                this.freqText = Utils.convertIntArray(this.freq);
            } catch (Exception e) {
                device = "";
            }
        } else if (device.contains("autodetect")) {
            try {
                Log.d("SpeedUp", "Autodetecting Frequencies");
                read = Utils.readFile("/sys/devices/system/cpu/cpu0/cpufreq/scaling_available_frequencies");
                if (read == "" || read == null) {
                    for (int i = 0; i <= 20; i++) {
                        this.freqText = Utils.autodetect();
                        if (this.freqText != null) {
                            break;
                        }
                        this.cpufreq.fixPermissions();
                        Thread.sleep(50);
                    }
                } else if (!(read == "" || read == null)) {
                    this.freqText = read.trim().split(" ");
                }
                if (this.freqText != null) {
                    this.freq = Utils.convertStringArrayToInteger(this.freqText);
                    Arrays.sort(this.freq, Collections.reverseOrder());
                    this.freqText = Utils.convertIntArray(this.freq);
                    return;
                }
                device = "";
            } catch (Exception e2) {
                Log.d("SpeedUp", "Error in detecting frequency list: " + e2);
                device = "";
            }
        }
    }
}
