package com.ansoft.speedup;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

public class PerflockDisable {
    Context context;
    Resources res;

    private class Disable implements Runnable {
        private Disable() {
        }

        void start() {
            new Thread(this).start();
        }

        public void run() {
            PerflockDisable.this.disable();
        }
    }

    public void doDisable(Context context, Resources res) {
        this.context = context;
        this.res = res;
        new Disable().start();
    }

    private void disable() {
        try {
            Log.d("perflock_disabler", "Getting target address");
            String lineContains = getLineContains("/proc/kallsyms", "perflock_notifier_call");
            if (lineContains != null) {
                InputStream ins;
                Log.d("perflock_disabler", "Found address: " + lineContains);
                String addr = lineContains.trim().split(" ")[0];
                Log.d("perflock_disabler", "Extracting module");
                File dir = this.context.getDir("module", 2);
                VermagicBuilder builder = new VermagicBuilder();
                String moduleName = builder.getModuleName();
                if (moduleName.contains("29")) {
                    Log.d("perflock_disabler", "Found a 2.6.29 HTC kernel");
                    ins = this.res.openRawResource(R.raw.perflock_disable29);
                } else if (moduleName.contains("3215")) {
                    Log.d("perflock_disabler", "Found a 2.6.32.15 HTC kernel");
                    ins = this.res.openRawResource(R.raw.perflock_disable3215);
                } else if (moduleName.contains("3217")) {
                    Log.d("perflock_disabler", "Found a 2.6.32.17 HTC kernel");
                    ins = this.res.openRawResource(R.raw.perflock_disable3217);
                } else if (moduleName.contains("3221")) {
                    Log.d("perflock_disabler", "Found a 2.6.32.21 HTC kernel");
                    ins = this.res.openRawResource(R.raw.perflock_disable3221);
                } else if (moduleName.contains("3510")) {
                    Log.d("perflock_disabler", "Found a 2.6.35.10 HTC kernel");
                    ins = this.res.openRawResource(R.raw.perflock_disable3510);
                } else if (moduleName.contains("359")) {
                    Log.d("perflock_disabler", "Found a 2.6.35.9 HTC kernel");
                    ins = this.res.openRawResource(R.raw.perflock_disable359);
                } else {
                    throw new Exception("KernelIncompatible");
                }
                byte[] buffer = new byte[ins.available()];
                ins.read(buffer);
                ins.close();
                FileOutputStream fos = new FileOutputStream(dir.getAbsolutePath() + "/" + moduleName);
                fos.write(buffer);
                fos.close();
                Log.d("perflock_disabler", "Configuring module");
                builder.writeVermagic(dir.getAbsolutePath() + "/" + moduleName);
                chmod(dir.getAbsolutePath() + "/" + moduleName);
                Log.d("perflock_disabler", "Loading module");
                runRootCommand("insmod " + dir.getAbsolutePath() + "/" + moduleName + " perflock_notifier_call_addr=0x" + addr);
                Log.d("perflock_disabler", "Module loaded");
                new File(dir.getAbsolutePath() + "/" + moduleName).delete();
                return;
            }
            Log.d("perflock_disabler", "Failed to find target address. Perflock is incompatible version or not enabled.");
        } catch (Exception e) {
            Log.d("perflock_disabler", "Operation aborted. Exception: " + e);
        }
    }

    private String getLineContains(String filename, String contain) {
        Throwable th;
        DataInputStream in = null;
        String line = "";
        try {
            DataInputStream in2 = new DataInputStream(new FileInputStream(filename));
            while (!line.contains(contain) && line != null) {
                try {
                    line = in2.readLine();
                } catch (Exception e) {
                    in = in2;
                } catch (Throwable th2) {
                    th = th2;
                    in = in2;
                }
            }
            try {
                in2.close();
                in = in2;
                return line;
            } catch (Exception e2) {
                in = in2;
                return null;
            }
        } catch (Exception e3) {
            try {
                in.close();
                return null;
            } catch (Exception e4) {
                return null;
            }
        } catch (Throwable th3) {
            th = th3;
            try {
                in.close();
                try {
                    throw th;
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
            } catch (Exception e5) {
                return null;
            }
        }
        return line;
    }

    private void runRootCommand(String command) {
        try {
            Process process = Runtime.getRuntime().exec("su");
            DataOutputStream os = new DataOutputStream(process.getOutputStream());
            os.writeBytes(command + "\n");
            os.writeBytes("exit\n");
            os.flush();
            os.close();
            process.waitFor();
            process.destroy();
        } catch (Exception e) {
            Log.d("perflock_disabler", "Operation aborted. Exception: " + e);
        }
    }

    private void chmod(String filename) {
        try {
            Process process = Runtime.getRuntime().exec("su");
            DataOutputStream os = new DataOutputStream(process.getOutputStream());
            DataInputStream read = new DataInputStream(process.getInputStream());
            os.writeBytes("chmod 777 " + filename + "\n");
            os.writeBytes("exit\n");
            os.flush();
            os.close();
            read.close();
            process.waitFor();
            process.destroy();
        } catch (Exception e) {
            Log.d("perflock_disabler", "Operation aborted. Exception: " + e);
        }
    }
}
