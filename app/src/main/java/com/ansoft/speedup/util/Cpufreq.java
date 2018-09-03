package com.ansoft.speedup.util;

import android.content.Context;
import android.preference.PreferenceManager;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class Cpufreq {
    public static final String CPUFREQ_DIR = "/sys/devices/system/cpu/cpu0/cpufreq/";
    public static final String CPUFREQ_ROOT_DIR = "/sys/devices/system/cpu/cpu0/";
    public static final String GOVERNOR_DIR_NEW = "/sys/devices/system/cpu/cpufreq/";
    public static final String GOVERNOR_DIR_OLD = "/sys/devices/system/cpu/cpu0/cpufreq/";
    public static final String IOSCHED_PATH = "/sys/block/mmcblk0/queue/scheduler";
    public static final String MSM_MFREQ = "/sys/devices/system/cpu/mfreq";
    String GOVERNOR_DIR;
    private int NUM_CORES;
    String governor;
    Context mContext;
    boolean mode;
    ArrayList<Integer> offlineCores;
    RootStream stream;
    String yeshup;

    public class RootStream {
        BufferedOutputStream bos;
        DataInputStream in;
        DataOutputStream os;
        Process process;

        RootStream(String shell) throws IOException {
            this.process = Runtime.getRuntime().exec(Cpufreq.this.yeshup + " " + shell);
            this.os = new DataOutputStream(this.process.getOutputStream());
            this.bos = new BufferedOutputStream(this.os);
        }

        public synchronized void writeLine(String command) throws IOException {
            this.bos.write(command.getBytes());
            this.bos.flush();
        }

        public synchronized void flush() throws IOException {
            this.bos.flush();
        }

        public synchronized void close() throws IOException, InterruptedException {
            try {
                this.process.exitValue();
            } catch (IllegalThreadStateException e) {
                this.bos.write("exit\n".getBytes());
                this.bos.flush();
                this.bos.close();
                this.process.waitFor();
            }
        }
    }

    public int getNumCores() {
        return this.NUM_CORES;
    }

    public Cpufreq(Context context, boolean write) {
        this.GOVERNOR_DIR = GOVERNOR_DIR_NEW;
        this.NUM_CORES = 1;
        this.mContext = context;
        this.mode = write;
        this.governor = getGovernor() + "/";
        this.yeshup = Utils.getYeshup(context);
        if (!new File(this.GOVERNOR_DIR + this.governor).exists()) {
            this.GOVERNOR_DIR = GOVERNOR_DIR_OLD;
        }
        if (write) {
            try {
                this.stream = new RootStream("su");
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                this.stream = new RootStream("sh");
            } catch (IOException e2) {
                e2.printStackTrace();
            }
        }
        if (write && !(checkPermissions() && checkParamPermissions())) {
            fixPermissions();
            fixGovernorParam();
        }
        String[] cpu = new File(Utils.CPUFREQ_DIR).list();
        int cores = 0;
        int i = 0;
        while (i < cpu.length) {
            if (cpu[i].length() == 4 && cpu[i].contains("cpu") && cpu[i].substring(3, 4).matches("^[-+]?\\d+(\\.\\d+)?$")) {
                cores++;
            }
            i++;
        }
        this.NUM_CORES = cores;
    }

    public String[] getIoSchedulers() {
        String[] strArr = null;
        try {
            String schedulers = Utils.readFile(IOSCHED_PATH).trim();
            if (schedulers != null) {
                strArr = schedulers.replaceAll("\\[", "").replaceAll("\\]", "").split(" ");
            }
        } catch (NullPointerException e) {
        }
        return strArr;
    }

    public boolean setIoScheduler(String scheduler) {
        try {
            this.stream.writeLine("echo " + scheduler + " > " + IOSCHED_PATH + "\n");
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public String getIoScheduler() {
        String str = null;
        String schedulers = Utils.readFile(IOSCHED_PATH);
        if (schedulers != null) {
            try {
                str = schedulers.substring(schedulers.indexOf(91) + 1, schedulers.lastIndexOf(93));
            } catch (IndexOutOfBoundsException e) {
            }
        }
        return str;
    }

    public void refreshGovernor() {
        if (this.mode && !checkParamPermissions()) {
            fixGovernorParam();
        }
        this.governor = getGovernor() + "/";
    }

    public String[] getGovernors() {
        try {
            return Utils.readFile("/sys/devices/system/cpu/cpu0/cpufreq/scaling_available_governors").trim().split(" ");
        } catch (NullPointerException e) {
            return null;
        }
    }

    public File[] getGovernorParamFiles() {
        return new File(this.GOVERNOR_DIR + this.governor).listFiles();
    }

    public void setMax(int max) {
        write("/sys/devices/system/cpu/cpu0/cpufreq/scaling_max_freq", Integer.toString(max));
    }

    public void setMin(int min) {
        write("/sys/devices/system/cpu/cpu0/cpufreq/scaling_min_freq", Integer.toString(min));
    }

    public void setGovernor(String governor) {
        try {
            this.stream.writeLine("echo " + governor + " > " + GOVERNOR_DIR_OLD + "scaling_governor" + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getMax() {
        return read("/sys/devices/system/cpu/cpu0/cpufreq/scaling_max_freq");
    }

    public String getMin() {
        return read("/sys/devices/system/cpu/cpu0/cpufreq/scaling_min_freq");
    }

    public String getGovernor() {
        try {
            return read("/sys/devices/system/cpu/cpu0/cpufreq/scaling_governor").trim();
        } catch (Exception e) {
            return "driver";
        }
    }

    public void setGovernorParam(String param, String value) {
        writeReal(this.GOVERNOR_DIR + this.governor + param, value);
    }

    public void setGovernorParam(String param, long value) {
        writeReal(this.GOVERNOR_DIR + this.governor + param, Long.toString(value));
    }

    public int setGovernorFromSaved(String governor, String filesDirPath) {
        if (!new File(filesDirPath + "/governor/" + governor.trim()).exists()) {
            return 1;
        }
        for (String line : Utils.readFile(filesDirPath + "/governor/" + governor.trim()).split("\n")) {
            String[] params = line.split(" ");
            setGovernorParam(params[0], params[1]);
        }
        return 0;
    }

    public long getGovernorParam(String param) {
        try {
            return Long.parseLong(read(this.GOVERNOR_DIR + this.governor + param).trim());
        } catch (Exception e) {
            return -1;
        }
    }

    public void setUserspaceSpeed(int speed) {
        if (getGovernor() == "userspace") {
            write("/sys/devices/system/cpu/cpu0/cpufreq/scaling_setspeed", Integer.toString(speed));
        }
    }

    public String[] getGovernorParams() {
        File dir = new File(this.GOVERNOR_DIR + this.governor);
        if (dir.exists()) {
            return dir.list();
        }
        return null;
    }

    public String[] getSamplingRateBounds() {
        return null;
    }

    public void setAdvanced(String contents) {
        String[] split = contents.split("\n");
        for (String split2 : split) {
            String[] read = split2.split(" ");
            setGovernorParam(read[0], read[1]);
        }
    }

    private void write(String file, String text) {
        runAllCores("echo " + text + " " + file, this.NUM_CORES);
    }

    private void writeReal(String file, String text) {
        try {
            this.stream.writeLine("echo " + text + " > " + file + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String read(String file) {
        return Utils.readFile(file);
    }

    private void fixGovernorParam() {
        try {
            this.stream.writeLine("chmod 777 " + this.GOVERNOR_DIR + this.governor + "/\n");
            this.stream.writeLine("chmod 666 " + this.GOVERNOR_DIR + this.governor + "/*\n");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void fixPermissions() {
        try {
            int i;
            if (PreferenceManager.getDefaultSharedPreferences(this.mContext).getBoolean("chmod_exclusive", false)) {
                int uid = Utils.getUid(this.mContext);
                for (i = 0; i < this.NUM_CORES; i++) {
                    this.stream.writeLine("chown " + uid + ":" + uid + " " + GOVERNOR_DIR_OLD.replaceAll("cpu0", "cpu" + i) + "\n");
                    this.stream.writeLine("chown " + uid + ":" + uid + " " + GOVERNOR_DIR_OLD.replaceAll("cpu0", "cpu" + i) + "*\n");
                    this.stream.writeLine("chown " + uid + ":" + uid + " " + GOVERNOR_DIR_OLD.replaceAll("cpu0", "cpu" + i) + "/*\n");
                    this.stream.writeLine("chown " + uid + ":" + uid + " " + GOVERNOR_DIR_OLD.replaceAll("cpu0", "cpu" + i) + "stats/\n");
                    this.stream.writeLine("chown " + uid + ":" + uid + " " + GOVERNOR_DIR_OLD.replaceAll("cpu0", "cpu" + i) + "stats/*\n");
                    this.stream.writeLine("chmod 777nativeAcquireCpuPerfWakeLock " + GOVERNOR_DIR_OLD.replaceAll("cpu0", "cpu" + i) + "\n");
                    this.stream.writeLine("chmod 644 " + GOVERNOR_DIR_OLD.replaceAll("cpu0", "cpu" + i) + "*\n");
                    this.stream.writeLine("chmod 644 " + GOVERNOR_DIR_OLD.replaceAll("cpu0", "cpu" + i) + "/*\n");
                    this.stream.writeLine("chmod 777 " + GOVERNOR_DIR_OLD.replaceAll("cpu0", "cpu" + i) + "stats/\n");
                    this.stream.writeLine("chmod 644 " + GOVERNOR_DIR_OLD.replaceAll("cpu0", "cpu" + i) + "stats/*\n");
                }
                return;
            }
            for (i = 0; i < this.NUM_CORES; i++) {
                this.stream.writeLine("chmod 777 " + GOVERNOR_DIR_OLD.replaceAll("cpu0", "cpu" + i) + "\n");
                this.stream.writeLine("chmod 666 " + GOVERNOR_DIR_OLD.replaceAll("cpu0", "cpu" + i) + "*\n");
                this.stream.writeLine("chmod 666 " + GOVERNOR_DIR_OLD.replaceAll("cpu0", "cpu" + i) + "/*\n");
                this.stream.writeLine("chmod 777 " + GOVERNOR_DIR_OLD.replaceAll("cpu0", "cpu" + i) + "stats/\n");
                this.stream.writeLine("chmod 666 " + GOVERNOR_DIR_OLD.replaceAll("cpu0", "cpu" + i) + "stats/*\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void fixOnlinePermissions() {
        int i = 0;
        while (i < this.NUM_CORES) {
            try {
                int uid = Utils.getUid(this.mContext);
                this.stream.writeLine("chown " + uid + ":" + uid + " " + CPUFREQ_ROOT_DIR.replaceAll("cpu0", "cpu" + i) + "online\n");
                i++;
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
        }
    }

    public boolean checkPermissions() {
        if (new File("/sys/devices/system/cpu/cpu0/cpufreq/scaling_max_freq").canWrite()) {
            return true;
        }
        return false;
    }

    public boolean hasParams() {
        if (new File(this.GOVERNOR_DIR + this.governor).exists()) {
            return true;
        }
        return false;
    }

    public boolean checkParamPermissions() {
        if (getGovernorParams() == null || new File(this.GOVERNOR_DIR + this.governor + getGovernorParams()[0]).canWrite()) {
            return true;
        }
        return false;
    }

    public void kill() {
        try {
            if (this.stream != null) {
                this.stream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e2) {
            e2.printStackTrace();
        }
    }

    public void runAllCores(String command, int cores) {
        for (int i = 0; i < cores; i++) {
            try {
                this.stream.writeLine(command.replaceAll("cpu0", "cpu" + i) + "\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void runCommand(String command) {
        try {
            this.stream.writeLine(command + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setFrequency(int max, int min) {
        if (max >= min) {
            try {
                if (this.NUM_CORES > 1 && new File(MSM_MFREQ).exists() && !new File("/sys/devices/system/cpu/cpu1/cpufreq").exists()) {
                    fixOnlinePermissions();
                    runAllCores("echo 1 > /sys/devices/system/cpu/cpu0/online", this.NUM_CORES);
                    fixPermissions();
                } else if (this.NUM_CORES > 1 && new File("/sys/devices/system/cpu/cpu1/online").exists()) {
                    fixOnlinePermissions();
                    runAllCores("echo 1 > /sys/devices/system/cpu/cpu0/online", this.NUM_CORES);
                    fixPermissions();
                }
                runAllCores("echo " + max + " > /sys/devices/system/cpu/cpu0/cpufreq/scaling_max_freq", this.NUM_CORES);
                runAllCores("echo " + min + " > /sys/devices/system/cpu/cpu0/cpufreq/scaling_min_freq", this.NUM_CORES);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void lockCoreOnline(int core) {
        try {
            this.stream.writeLine("chmod 444 " + GOVERNOR_DIR_OLD.replaceAll("cpu0", "cpu" + core) + "online\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void unlockCoreOnline(int core) {
        try {
            this.stream.writeLine("chmod 666 " + GOVERNOR_DIR_OLD.replaceAll("cpu0", "cpu" + core) + "online\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void bringCoreOnline(int core) {
        try {
            this.stream.writeLine("echo 1 > " + GOVERNOR_DIR_OLD.replaceAll("cpu0", "cpu" + core) + "online\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void bringCoreOffline(int core) {
        try {
            this.stream.writeLine("echo 0 > " + GOVERNOR_DIR_OLD.replaceAll("cpu0", "cpu" + core) + "online\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void waitOnline(int core, int timeout) {
    }
}
