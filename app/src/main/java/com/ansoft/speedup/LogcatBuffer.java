package com.ansoft.speedup;

import android.content.Context;
import android.os.Build.VERSION;
import android.os.Process;
import android.util.Log;
import com.ansoft.speedup.util.Utils;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.CharBuffer;

public class LogcatBuffer implements Runnable {
    CharBuffer buffer;
    private OnLineReadListener listener;
    private String options;
    private BufferedReader reader;
    private boolean run;
    private Thread thread;
    String yeshup;

    public interface OnLineReadListener {
        void onLineRead(String str);
    }

    public LogcatBuffer(Context context) {
        this.run = true;
        this.options = null;
        this.thread = new Thread(this);
        this.yeshup = Utils.getYeshup(context);
        this.thread.start();
    }

    public LogcatBuffer(String options, Context context) {
        this.run = true;
        this.options = null;
        this.options = options;
        this.thread = new Thread(this);
        this.yeshup = Utils.getYeshup(context);
        this.thread.start();
    }

    public void setOnLineReadListener(OnLineReadListener listener) {
        this.listener = listener;
    }

    public void run() {
        Process.setThreadPriority(10);
        java.lang.Process process = null;
        try {
            process = Runtime.getRuntime().exec(this.yeshup + " logcat -b events -c");
            if (process.waitFor() != 0) {
                this.run = false;
                Log.e("SpeedUp", "Error: could not scan for running apps.");
            }
            if (VERSION.SDK_INT >= 16) {
                process = Runtime.getRuntime().exec(this.yeshup + " su");
                process.getOutputStream().write((this.yeshup + " logcat " + this.options + "\n").getBytes());
            } else {
                process = Runtime.getRuntime().exec(this.yeshup + " logcat " + this.options);
            }
            this.reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
            this.run = false;
        } catch (InterruptedException e2) {
            e2.printStackTrace();
            this.run = false;
        } catch (NullPointerException e3) {
            e3.printStackTrace();
            this.run = false;
        }
        while (this.run) {
            try {
                String read = this.reader.readLine();
                if (read == null) {
                    stop();
                    this.listener.onLineRead(null);
                    this.listener = null;
                    this.thread = null;
                }
                if (this.listener != null) {
                    this.listener.onLineRead(read);
                }
            } catch (IOException e4) {
                e4.printStackTrace();
                this.run = false;
            }
        }
        if (process != null) {
            process.destroy();
            try {
                process.waitFor();
            } catch (InterruptedException e22) {
                e22.printStackTrace();
            }
        }
    }

    public void stop() {
        this.run = false;
    }

    public boolean isAlive() {
        return this.thread.isAlive();
    }
}
