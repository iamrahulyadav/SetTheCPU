package com.ansoft.speedup;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.accessibility.AccessibilityEventCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.ClipboardManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ansoft.speedup.util.Cpufreq;
import com.ansoft.speedup.util.Utils;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;

import org.joda.time.MutableDateTime;
import org.joda.time.chrono.EthiopicChronology;

public class InfoWindowActivity extends AppCompatActivity {
    static final String CPUIDLE = "/sys/devices/system/cpu/cpu0/cpuidle/";
    TextView content;
    Cpufreq cpufreq;
    Handler handler;
    LinearLayout outerLayout;
    Runnable refreshCounter;
    RefreshFunction rf;

    public class IdleState {
        private String name;
        private long timeUs;

        public void setName(String name) {
            this.name = name;
        }

        public void setTime(long time) {
            this.timeUs = time;
        }

        public String getName() {
            return this.name;
        }

        public long getTime() {
            return this.timeUs;
        }

        public long getTimeMs() {
            return this.timeUs / 1000;
        }
    }

    private interface RefreshFunction {
        void call();

        String getString();
    }

    private class BatteryRefresh implements RefreshFunction {

        /* renamed from: com.ansoft.setthecpu.InfoWindowActivity.BatteryRefresh.1 */
        class C00641 implements Runnable {
            C00641() {
            }

            public void run() {
                InfoWindowActivity.this.content.clearFocus();
                InfoWindowActivity.this.content.setText(InfoWindowActivity.getBatteryInfo());
                InfoWindowActivity.this.handler.postDelayed(InfoWindowActivity.this.refreshCounter, 1000);
            }
        }

        private BatteryRefresh() {
        }

        public void call() {
            InfoWindowActivity.this.refreshCounter = new C00641();
            InfoWindowActivity.this.handler.post(InfoWindowActivity.this.refreshCounter);
        }

        public String getString() {
            return InfoWindowActivity.getBatteryInfo();
        }
    }

    private class CpuRefresh implements RefreshFunction {

        /* renamed from: com.ansoft.setthecpu.InfoWindowActivity.CpuRefresh.1 */
        class C00651 implements Runnable {
            C00651() {
            }

            public void run() {
                InfoWindowActivity.this.content.clearFocus();
                InfoWindowActivity.this.content.setText(InfoWindowActivity.getCpuInfo());
                InfoWindowActivity.this.handler.postDelayed(InfoWindowActivity.this.refreshCounter, 1000);
            }
        }

        private CpuRefresh() {
        }

        public void call() {
            InfoWindowActivity.this.refreshCounter = new C00651();
            InfoWindowActivity.this.handler.post(InfoWindowActivity.this.refreshCounter);
        }

        public String getString() {
            return InfoWindowActivity.getCpuInfo();
        }
    }

    private class KernelRefresh implements RefreshFunction {

        /* renamed from: com.ansoft.setthecpu.InfoWindowActivity.KernelRefresh.1 */
        class C00661 implements Runnable {
            C00661() {
            }

            public void run() {
                InfoWindowActivity.this.content.clearFocus();
                InfoWindowActivity.this.content.setText(InfoWindowActivity.getKernelInfo());
            }
        }

        private KernelRefresh() {
        }

        public void call() {
            InfoWindowActivity.this.refreshCounter = new C00661();
            InfoWindowActivity.this.handler.post(InfoWindowActivity.this.refreshCounter);
        }

        public String getString() {
            return InfoWindowActivity.getKernelInfo();
        }
    }

    private class MemoryRefresh implements RefreshFunction {

        /* renamed from: com.ansoft.setthecpu.InfoWindowActivity.MemoryRefresh.1 */
        class C00671 implements Runnable {
            C00671() {
            }

            public void run() {
                InfoWindowActivity.this.content.clearFocus();
                InfoWindowActivity.this.content.setText(InfoWindowActivity.getMemoryInfo());
                InfoWindowActivity.this.handler.postDelayed(InfoWindowActivity.this.refreshCounter, 1000);
            }
        }

        private MemoryRefresh() {
        }

        public void call() {
            InfoWindowActivity.this.refreshCounter = new C00671();
            InfoWindowActivity.this.handler.post(InfoWindowActivity.this.refreshCounter);
        }

        public String getString() {
            return InfoWindowActivity.getMemoryInfo();
        }
    }

    private class TimeInStateRefresh implements RefreshFunction {

        /* renamed from: com.ansoft.setthecpu.InfoWindowActivity.TimeInStateRefresh.1 */
        class C00681 implements Runnable {
            C00681() {
            }

            public void run() {
                try {
                    InfoWindowActivity.this.content.clearFocus();
                    InfoWindowActivity.this.content.setText(InfoWindowActivity.this.getTimeInStateInfo());
                    InfoWindowActivity.this.outerLayout.removeViewAt(1);
                    InfoWindowActivity.this.handler.postDelayed(InfoWindowActivity.this.refreshCounter, 5000);
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            }
        }

        TimeInStateRefresh() {
            try {
                InfoWindowActivity.this.content.setTypeface(Typeface.MONOSPACE);
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }

        public void call() {
            InfoWindowActivity.this.refreshCounter = new C00681();
            InfoWindowActivity.this.handler.post(InfoWindowActivity.this.refreshCounter);
        }

        public String getString() {
            return InfoWindowActivity.this.getTimeInStateInfo();
        }
    }

    public InfoWindowActivity() {
        this.refreshCounter = null;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.infowindow);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        this.cpufreq = new Cpufreq(this, false);
        this.outerLayout = (LinearLayout) findViewById(R.id.outer_layout);
        this.content = (TextView) findViewById(R.id.content);
        this.rf = new KernelRefresh();
        this.handler = new Handler();
        String[] title = getResources().getStringArray(R.array.info_title);
        Bundle extras = getIntent().getExtras();
        getSupportActionBar().setTitle(title[extras.getInt("position")]);
        //setTitle(title[extras.getInt("position")]);
        switch (extras.getInt("position")) {
            case MutableDateTime.ROUND_NONE /*0*/:
                this.rf = new KernelRefresh();
                break;
            case EthiopicChronology.EE /*1*/:
                this.rf = new CpuRefresh();
                break;
            case MutableDateTime.ROUND_CEILING /*2*/:
                this.rf = new BatteryRefresh();
                break;
            case MutableDateTime.ROUND_HALF_FLOOR /*3*/:
                this.rf = new TimeInStateRefresh();
                break;
            case MutableDateTime.ROUND_HALF_CEILING /*4*/:
                this.rf = new MemoryRefresh();
                break;
        }
        this.rf.call();
    }

    public void onResume() {
        super.onResume();
        if (this.refreshCounter != null) {
            this.handler.post(this.refreshCounter);
        }
    }

    public void onPause() {
        super.onPause();
        if (this.refreshCounter != null) {
            this.handler.removeCallbacks(this.refreshCounter);
        }
    }

    public void onDestroy() {
        super.onDestroy();
        if (this.refreshCounter != null) {
            this.handler.removeCallbacks(this.refreshCounter);
        }
    }


    private static String getMemoryInfo() {
        return Utils.readFile("/proc/meminfo", '\u0000', new byte[AccessibilityEventCompat.TYPE_WINDOW_CONTENT_CHANGED]);
    }

    private String getTimeInStateInfo() {
        if (!new File("/sys/devices/system/cpu/cpu0/cpufreq/stats/time_in_state").canRead()) {
            this.cpufreq.fixPermissions();
        }
        return getResources().getString(R.string.speedtimepercent) + "\n" + getFormattedTimeInStatePercentages(Utils.readFile("/sys/devices/system/cpu/cpu0/cpufreq/stats/time_in_state"));
    }

    private static String getBatteryInfo() {
        int temp = getBatteryTemp();
        float c = Float.valueOf((float) temp).floatValue() / 10.0f;
        float f = Float.valueOf((float) ((int) (320.0d + (1.8d * ((double) temp))))).floatValue() / 10.0f;
        return "Battery Temp: " + c + " \u00b0C (" + f + " \u00b0F)\n" + "Battery Level: " + getBatteryLevel() + "%";
    }

    private static String getCpuInfo() {
        String omapTemp = Utils.readFile("/sys/class/hwmon/hwmon0/device/temp1_input", '\n');
        String cpuTemp = "";
        if (!(omapTemp == "" || omapTemp == null)) {
            int omapTempC = Integer.parseInt(omapTemp);
            if (omapTempC > 200) {
                omapTempC /= 100;
            }
            cpuTemp = "CPU Temp Sensor: Detected\nCPU Temp: " + omapTempC + " \u00b0C (" + ((float) (32.0d + (((double) omapTempC) * 1.8d))) + " \u00b0F)\n";
        }
        return cpuTemp + "Frequency : " + readFrequency() + " KHz\nLoad Avg : " + Utils.readFile("/proc/loadavg") + "\n" + Utils.readFile("/proc/cpuinfo");
    }

    private static String getKernelInfo() {
        return "Board: " + Build.BOARD + "\n" + "Product: " + Build.PRODUCT + "\n" + "Model: " + Build.MODEL + "\n" + "Device: " + Build.DEVICE + "\n" + "Build: " + Build.DISPLAY + "\n" + Build.FINGERPRINT + "\n" + "Manufacturer: " + Build.MANUFACTURER + "\n" + "Brand: " + Build.BRAND + "\n" + "CPU ABI: " + Build.CPU_ABI + "\n" + "\nKernel\n" + Utils.readFile("/proc/version", '\u0000');
    }

    private static String getFormattedTimeInStatePercentages(String time_in_state) {
        try {
            int i;
            String[] split = time_in_state.trim().split("\n");
            String ret = "";
            long total = 0;
            for (String split2 : split) {
                total += Long.parseLong(split2.split(" ")[1]);
            }
            for (i = 0; i < split.length; i++) {
                String[] column = split[i].split(" ");
                ret = ret + rightPad(column[0], 10) + "|" + rightPad(column[1], 13) + "|" + rightPad(new DecimalFormat("#.##").format((100.0d * ((double) Long.parseLong(split[i].split(" ")[1]))) / ((double) total)) + "%", 6) + "\n";
            }
            return ret.trim();
        } catch (Exception e) {
            return "";
        }
    }

    private static String rightPad(String str, int pad) {
        if (str.length() >= pad) {
            return str;
        }
        StringBuffer sb = new StringBuffer(pad);
        sb.append(str);
        while (sb.length() < pad) {
            sb.append(' ');
        }
        return sb.toString();
    }

    private static double[] getFormattedTimeInState(String time_in_state) {
        try {
            String[] split = time_in_state.trim().split("\n");
            double[] dArr = new double[split.length];
            for (int i = 0; i < split.length; i++) {
                dArr[i] = (double) Long.parseLong(split[i].split(" ")[1]);
            }
            return dArr;
        } catch (Exception e) {
            return null;
        }
    }

    private static int readFrequency() {
        String curFreq = Utils.readFile("/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_cur_freq", '\n');
        for (int count = 0; count < 10; count++) {
            if (curFreq != null) {
                try {
                    if (curFreq != "") {
                        return Integer.parseInt(curFreq);
                    }
                } catch (NumberFormatException e) {
                }
            }
            curFreq = Utils.readFile("/sys/devices/system/cpu/cpu0/cpufreq/scaling_cur_freq", '\n');
            if (curFreq != null && curFreq != "") {
                return Integer.parseInt(curFreq);
            }
            curFreq = Utils.readFile("/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_cur_freq", '\n');
        }
        return 0;
    }

    private static int getBatteryLevel() {
        int i = 0;
        String read = Utils.readFile("/sys/class/power_supply/battery/capacity");
        if (read != null) {
            try {
                if (read != "") {
                    i = Integer.parseInt(read.trim());
                }
            } catch (Exception e) {
            }
        }
        return i;
    }

    private static int getBatteryTemp() {
        int i = 0;
        String read = Utils.readFile("/sys/class/power_supply/battery/temp");
        if (read == null || read == "") {
            read = Utils.readFile("/sys/class/power_supply/battery/batt_temp");
        }
        if (read != null) {
            try {
                if (read != "") {
                    i = Integer.parseInt(read.trim());
                }
            } catch (Exception e) {
            }
        }
        return i;
    }

    private static int[] getFormattedFreqList(String time_in_state) {
        try {
            String[] split = time_in_state.trim().split("\n");
            int[] iArr = new int[split.length];
            for (int i = 0; i < split.length; i++) {
                iArr[i] = Integer.parseInt(split[i].split(" ")[0]);
            }
            return iArr;
        } catch (Exception e) {
            return null;
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        new MenuInflater(this).inflate(R.menu.infomenu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 16908332:
                finish();
                return true;
            case R.id.copy:
                ((ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE)).setText(this.rf.getString());
                return true;
            case R.id.send:
                Intent sendIntent = new Intent("android.intent.action.SEND");
                sendIntent.putExtra("android.intent.extra.SUBJECT", getTitle());
                sendIntent.putExtra("android.intent.extra.TEXT", this.rf.getString());
                sendIntent.setType("text/plain");
                startActivity(Intent.createChooser(sendIntent, "Send..."));
                return true;
            default:
                return false;
        }
    }

    private ArrayList<IdleState> getIdleStates() {
        ArrayList<IdleState> list = new ArrayList();
        File cpuidle = new File(CPUIDLE);
        if (!cpuidle.exists() || !cpuidle.canRead() || !cpuidle.isDirectory()) {
            return null;
        }
        for (String s : cpuidle.list()) {
            File time = new File("/sys/devices/system/cpu/cpu0/cpuidle/time");
            if (time.exists() && time.canRead()) {
                IdleState is = new IdleState();
                is.setName(s);
                try {
                    is.setTime(Long.parseLong(Utils.readFile("/sys/devices/system/cpu/cpu0/cpuidle/time")));
                    list.add(is);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    return list;
                }
            }
        }
        return list;
    }
}
