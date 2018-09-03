package com.ansoft.speedup;

import android.util.Log;
import com.ansoft.speedup.util.Utils;
import java.io.IOException;
import java.io.RandomAccessFile;

public class VermagicBuilder {
    public String MODULE_NAME;
    public int VERMAGIC_LENGTH;
    public String VERMAGIC_MIDDLE;
    public int VERMAGIC_OFFSET_END;
    public int VERMAGIC_OFFSET_START;
    String verMagic;

    VermagicBuilder() {
        this.VERMAGIC_MIDDLE = "preempt mod_unload";
        this.verMagic = getVermagic();
        if (this.verMagic.contains("2.6.29")) {
            this.MODULE_NAME = "perflock_disable29.ko";
            this.VERMAGIC_OFFSET_START = 581;
            this.VERMAGIC_OFFSET_END = 649;
        } else if (this.verMagic.contains("2.6.32.15")) {
            this.MODULE_NAME = "perflock_disable3215.ko";
            this.VERMAGIC_OFFSET_START = 545;
            this.VERMAGIC_OFFSET_END = 619;
        } else if (this.verMagic.contains("2.6.32.17")) {
            this.MODULE_NAME = "perflock_disable3217.ko";
            this.VERMAGIC_OFFSET_START = 517;
            this.VERMAGIC_OFFSET_END = 583;
        } else if (this.verMagic.contains("2.6.32.21")) {
            this.MODULE_NAME = "perflock_disable3221.ko";
            this.VERMAGIC_OFFSET_START = 517;
            this.VERMAGIC_OFFSET_END = 583;
        } else if (this.verMagic.contains("2.6.35.10")) {
            this.MODULE_NAME = "perflock_disable3510.ko";
            this.VERMAGIC_OFFSET_START = 525;
            this.VERMAGIC_OFFSET_END = 575;
        } else if (this.verMagic.contains("2.6.35.9")) {
            this.MODULE_NAME = "perflock_disable359.ko";
            this.VERMAGIC_OFFSET_START = 525;
            this.VERMAGIC_OFFSET_END = 581;
        } else {
            this.MODULE_NAME = null;
            this.VERMAGIC_OFFSET_START = 0;
            this.VERMAGIC_OFFSET_END = 0;
        }
        this.VERMAGIC_LENGTH = this.VERMAGIC_OFFSET_END - this.VERMAGIC_OFFSET_START;
    }

    public boolean writeVermagic(String filepath) {
        try {
            RandomAccessFile ra = new RandomAccessFile(filepath, "rwd");
            while (this.verMagic.length() <= this.VERMAGIC_LENGTH) {
                this.verMagic += '\u0000';
            }
            byte[] buffer = this.verMagic.getBytes();
            ra.seek((long) this.VERMAGIC_OFFSET_START);
            ra.write(buffer, 0, buffer.length);
            ra.close();
            return true;
        } catch (IOException e) {
            Log.d("perflock_disabler", "Exception caught. ko handling failed: " + e);
            return false;
        }
    }

    private String getVermagic() {
        String unameR = getUnameR();
        String arch = getArch();
        if (unameR == null || arch == null) {
            return null;
        }
        return unameR + " " + this.VERMAGIC_MIDDLE + " " + arch + " ";
    }

    private String getUnameR() {
        String unameR = Utils.readFile("/proc/version").trim().split(" ")[2];
        try {
            if (unameR.contains("2.6.29") || unameR.contains("2.6.32.15") || unameR.contains("2.6.32.17") || unameR.contains("2.6.32.21") || unameR.contains("2.6.35.10") || unameR.contains("2.6.35.9")) {
                return unameR;
            }
            Log.d("perflock_disabler", "Incompatible kernel " + unameR + " does not match 2.6.29, 2.6.32.15, " + "2.6.32.17, 2.6.32.21, 2.6.35.10, 2.6.35.9");
            return null;
        } catch (Exception e) {
            Log.d("perflock_disabler", "Exception caught while reading kernel name: " + e);
            return null;
        }
    }

    public String getModuleName() {
        return this.MODULE_NAME.trim();
    }

    private String getArch() {
        try {
            String arch = Utils.readFile("/proc/cpuinfo").trim();
            if (arch.contains("ARMv6")) {
                return "ARMv6";
            }
            if (arch.contains("ARMv7")) {
                return "ARMv7";
            }
            Log.d("perflock_disabler", "Could not get arch in cpuinfo.");
            return null;
        } catch (Exception e) {
            Log.d("perflock_disabler", "Exception caught while reading arch: " + e);
            return null;
        }
    }
}
