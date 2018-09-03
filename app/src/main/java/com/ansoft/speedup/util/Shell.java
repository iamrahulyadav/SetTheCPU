package com.ansoft.speedup.util;

import android.content.Context;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Shell {
    BufferedInputStream mBin;
    BufferedOutputStream mBos;
    BufferedInputStream mErr;
    Process mProcess;
    String mYeshup;

    Shell(Context context, String shell, boolean useYeshup) throws IOException {
        this.mYeshup = "";
        if (useYeshup) {
            this.mYeshup = Utils.getYeshup(context);
        }
        this.mProcess = Runtime.getRuntime().exec(this.mYeshup + " " + shell);
        this.mBos = new BufferedOutputStream(new DataOutputStream(this.mProcess.getOutputStream()));
        this.mBin = new BufferedInputStream(new DataInputStream(this.mProcess.getInputStream()));
        this.mErr = new BufferedInputStream(new DataInputStream(this.mProcess.getErrorStream()));
    }

    public synchronized void writeLine(String command) throws IOException {
        this.mBos.write(command.getBytes());
        this.mBos.flush();
    }

    public synchronized byte[] read() throws IOException {
        byte[] read;
        if (this.mBin.available() > 0) {
            read = new byte[this.mBin.available()];
            this.mBin.read(read);
        } else {
            read = null;
        }
        return read;
    }

    public synchronized byte[] readErr() throws IOException {
        byte[] read;
        if (this.mErr.available() > 0) {
            read = new byte[this.mErr.available()];
            this.mErr.read(read);
        } else {
            read = null;
        }
        return read;
    }

    public synchronized void flush() throws IOException {
        this.mBos.flush();
    }

    public synchronized int close() throws IOException, InterruptedException {
        int exitValue;
        try {
            exitValue = this.mProcess.exitValue();
        } catch (IllegalThreadStateException e) {
            this.mBos.write("exit\n".getBytes());
            this.mBos.flush();
            this.mBos.close();
            this.mProcess.waitFor();
            exitValue = -1;
        }
        return exitValue;
    }
}
