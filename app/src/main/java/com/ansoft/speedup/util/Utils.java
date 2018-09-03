package com.ansoft.speedup.util;

import android.content.Context;
import android.os.Build;
import android.os.Build.VERSION;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.accessibility.AccessibilityEventCompat;

import com.ansoft.speedup.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.FileChannel;

public class Utils {
    public static final String CPUFREQ_DIR = "/sys/devices/system/cpu/";
    static byte[] buffer;
    static FileInputStream is;

    static {
        buffer = new byte[AccessibilityEventCompat.TYPE_TOUCH_EXPLORATION_GESTURE_END];
    }

    public static boolean ifFileExists(String path) {
        return new File(path).exists();
    }

    public static int getUid(Context context) {
        return context.getApplicationInfo().uid;
    }

    public static String readFile(String file, char endChar) {
        try {
            is = new FileInputStream(file);
            int len = is.read(buffer);
            is.close();
            if (len > 0) {
                int i = 0;
                while (i < len && buffer[i] != endChar) {
                    i++;
                }
                return new String(buffer, 0, 0, i);
            }
        } catch (FileNotFoundException e) {
        } catch (IOException e2) {
        }
        return null;
    }

    public static String readFile(String file) {
        try {
            is = new FileInputStream(file);
            int len = is.read(buffer);
            is.close();
            if (len > 0) {
                int i = 0;
                while (i < len && buffer[i] != 0) {
                    i++;
                }
                return new String(buffer, 0, 0, i);
            }
        } catch (FileNotFoundException e) {
        } catch (IOException e2) {
        }
        return null;
    }

    public static String readFile(String file, char endChar, byte[] buffer) {
        try {
            is = new FileInputStream(file);
            int len = is.read(buffer);
            is.close();
            if (len > 0) {
                int i = 0;
                while (i < len && buffer[i] != endChar) {
                    i++;
                }
                return new String(buffer, 0, 0, i);
            }
        } catch (FileNotFoundException e) {
        } catch (IOException e2) {
        }
        return null;
    }

    public static void lockFile() {
    }

    public static String[] autodetect() {
        try {
            String time_in_state = readFile("/sys/devices/system/cpu/cpu0/cpufreq/stats/time_in_state");
            if (time_in_state == null || time_in_state == "") {
                time_in_state = readFile("/sys/devices/system/cpu/cpu0/cpufreq/frequency_voltage_table");
            }
            if (time_in_state == null || time_in_state == "") {
                time_in_state = readFile("/sys/devices/system/cpu/cpu0/cpufreq/FakeShmoo_freq_voltage_table");
            }
            if (time_in_state == null || time_in_state == "") {
                time_in_state = readFile("/sys/devices/system/cpu/cpu0/cpufreq/freq_voltage_table");
            }
            if (time_in_state == null) {
                return null;
            }
            time_in_state.trim();
            String[] split = time_in_state.split("\n");
            for (int i = 0; i < split.length; i++) {
                String retCheck = split[i].split(" ")[0];
                try {
                    Integer.parseInt(retCheck);
                    split[i] = retCheck;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return split;
        } catch (Exception e2) {
            e2.printStackTrace();
            return null;
        }
    }

    public static void copyFile(File src, File dst) throws IOException {
        FileChannel outChannel;
        FileChannel inChannel = new FileInputStream(src).getChannel();
        if (dst.exists()) {
            outChannel = new FileOutputStream(dst).getChannel();
        } else {
            outChannel = new FileOutputStream(dst).getChannel();
        }
        try {
            inChannel.transferTo(0, inChannel.size(), outChannel);
            if (inChannel != null) {
                inChannel.close();
            }
            if (outChannel != null) {
                outChannel.close();
            }
        } catch (Throwable th) {
            if (inChannel != null) {
                inChannel.close();
            }
            if (outChannel != null) {
                outChannel.close();
            }
        }
    }

    public static Integer[] convertStringArray(String[] sarray) {
        if (sarray == null) {
            return null;
        }
        Integer[] numArr = new Integer[sarray.length];
        for (int i = 0; i < sarray.length; i++) {
            numArr[i] = Integer.valueOf(Integer.parseInt(sarray[i].trim()));
        }
        return numArr;
    }

    public static String[] convertIntArray(int[] sarray) {
        if (sarray == null) {
            return null;
        }
        String[] strArr = new String[sarray.length];
        for (int i = 0; i < sarray.length; i++) {
            strArr[i] = "" + sarray[i];
        }
        return strArr;
    }

    public static Integer[] convertStringArrayToInteger(String[] sarray) {
        if (sarray == null) {
            return null;
        }
        Integer[] numArr = new Integer[sarray.length];
        for (int i = 0; i < sarray.length; i++) {
            numArr[i] = Integer.valueOf(Integer.parseInt(sarray[i].trim()));
        }
        return numArr;
    }

    public static String[] convertIntArray(Integer[] sarray) {
        if (sarray == null) {
            return null;
        }
        String[] strArr = new String[sarray.length];
        for (int i = 0; i < sarray.length; i++) {
            strArr[i] = "" + sarray[i];
        }
        return strArr;
    }

    public static int getIndex(String f, String[] array) {
        if (f == null || array == null) {
            return 0;
        }
        int i = 0;
        while (i < array.length) {
            if (array[i].equals(f) || f.equals(array[i])) {
                return i;
            }
            i++;
        }
        return 0;
    }

    public static String getYeshup(Context context) {
        InputStream ins;
        File yeshup = new File(context.getDir("bin", 0).getAbsolutePath() + "/yeshup");
        if (yeshup.exists()) {
            try {
                if (Runtime.getRuntime().exec(yeshup.getAbsolutePath()).waitFor() == MotionEventCompat.ACTION_MASK) {
                    return yeshup.getAbsolutePath();
                }
                throw new IOException();
            } catch (IOException e) {
                try {
                    if (Runtime.getRuntime().exec("chmod 744 " + yeshup.getAbsolutePath()).waitFor() != 0) {
                        throw new IOException();
                    } else if (Runtime.getRuntime().exec(yeshup.getAbsolutePath()).waitFor() == MotionEventCompat.ACTION_MASK) {
                        return yeshup.getAbsolutePath();
                    } else {
                        throw new IOException();
                    }
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            } catch (InterruptedException e2) {
            }
        }
        String CPU_ABI2 = null;
        if (VERSION.SDK_INT >= 8) {
            try {
                CPU_ABI2 = Build.CPU_ABI2;
            } catch (NoSuchFieldError e4) {
            }
        }
        if ("armeabi-v7a".equals(Build.CPU_ABI) || "armeabi-v7a".equals(CPU_ABI2)) {
            ins = context.getResources().openRawResource(R.raw.yeshup_armeabiv7a);
        } else if (Build.CPU_ABI.contains("armeabi") || CPU_ABI2.contains("armeabi")) {
            ins = context.getResources().openRawResource(R.raw.yeshup_armeabi);
        } else if ("x86".equals(Build.CPU_ABI) || "x86".equals(CPU_ABI2)) {
            ins = context.getResources().openRawResource(R.raw.yeshup_x86);
        } else if ("mips".equals(Build.CPU_ABI) || "mips".equals(CPU_ABI2)) {
            ins = context.getResources().openRawResource(R.raw.yeshup_mips);
        } else {
            ins = context.getResources().openRawResource(R.raw.yeshup_armeabi);
        }
        try {
            byte[] buffer = new byte[ins.available()];
            ins.read(buffer);
            ins.close();
            FileOutputStream fos = new FileOutputStream(yeshup.getAbsolutePath());
            fos.write(buffer);
            fos.close();
            if (Runtime.getRuntime().exec("chmod 744 " + yeshup.getAbsolutePath()).waitFor() != 0) {
                throw new IOException();
            } else if (Runtime.getRuntime().exec(yeshup.getAbsolutePath()).waitFor() == MotionEventCompat.ACTION_MASK) {
                return yeshup.getAbsolutePath();
            } else {
                throw new IOException();
            }
        } catch (IOException e5) {
            e5.printStackTrace();
            return "";
        } catch (InterruptedException e6) {
            e6.printStackTrace();
            return "";
        }
    }
}
