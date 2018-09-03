package com.ansoft.speedup;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

public class PerflockActivity extends Activity {
    ImageView buttonClose;
    Button buttonDisable;
    CheckBox checkBoot;
    Editor editor;
    ProgressDialog pd;
    SharedPreferences settings;

    /* renamed from: com.ansoft.setthecpu.PerflockActivity.1 */
    class C00701 implements OnClickListener {
        C00701() {
        }

        public void onClick(View v) {
            PerflockActivity.this.editor.putBoolean("perflockBoot", !PerflockActivity.this.settings.getBoolean("perflockBoot", false));
            PerflockActivity.this.editor.commit();
            Log.d("perflock_disabler", "Perflock on boot is " + PerflockActivity.this.settings.getBoolean("perflockBoot", false));
        }
    }

    /* renamed from: com.ansoft.setthecpu.PerflockActivity.2 */
    class C00712 implements DialogInterface.OnClickListener {
        C00712() {
        }

        public void onClick(DialogInterface arg0, int arg1) {
            PerflockActivity.this.doDisable();
        }
    }

    /* renamed from: com.ansoft.setthecpu.PerflockActivity.3 */
    class C00723 implements OnCancelListener {
        C00723() {
        }

        public void onCancel(DialogInterface dialog) {
            PerflockActivity.this.finish();
        }
    }

    /* renamed from: com.ansoft.setthecpu.PerflockActivity.4 */
    class C00734 implements OnCancelListener {
        final /* synthetic */ Disable val$disable;

        C00734(Disable disable) {
            this.val$disable = disable;
        }

        public void onCancel(DialogInterface dialog) {
            this.val$disable.quit = true;
        }
    }

    private class Disable implements Runnable {
        String errorMsg;
        private Handler handler;
        public boolean quit;
        boolean setCheckBoot;

        /* renamed from: com.ansoft.setthecpu.PerflockActivity.Disable.1 */
        class C00741 extends Handler {
            C00741() {
            }

            public void handleMessage(Message msg) {
                PerflockActivity.this.pd.dismiss();
                if (Disable.this.setCheckBoot) {
                    PerflockActivity.this.showDialog(true);
                } else {
                    PerflockActivity.this.showDialog(false);
                }
            }
        }

        private Disable() {
            this.quit = false;
            this.handler = new C00741();
        }

        private String getLineContains(String filename, String contain) {
            Throwable th;
            DataInputStream in = null;
            String line = "";
            try {
                DataInputStream in2 = new DataInputStream(new FileInputStream(filename));
                while (!line.contains(contain) && line != null) {
                    try {
                        if (this.quit) {
                            try {
                                in2.close();
                                in = in2;
                                return null;
                            } catch (Exception e) {
                                in = in2;
                                return null;
                            }
                        }
                        line = in2.readLine();
                    } catch (Exception e2) {
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
                } catch (Exception e3) {
                    in = in2;
                    return null;
                }
            } catch (Exception e4) {
                try {
                    in.close();
                    return null;
                } catch (Exception e5) {
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
                } catch (Exception e6) {
                    return null;
                }
            }
            return line;
        }

        void start() {
            new Thread(this).start();
        }

        public void run() {
            try {
                Log.d("perflock_disabler", "Getting target address");
                String lineContains = getLineContains("/proc/kallsyms", "perflock_notifier_call");
                if (lineContains != null) {
                    InputStream ins;
                    Log.d("perflock_disabler", "Found address: " + lineContains);
                    String addr = lineContains.trim().split(" ")[0];
                    Log.d("perflock_disabler", "Extracting module");
                    File dir = PerflockActivity.this.getApplicationContext().getDir("module", 2);
                    VermagicBuilder builder = new VermagicBuilder();
                    String moduleName = builder.getModuleName();
                    if (moduleName.contains("29")) {
                        Log.d("perflock_disabler", "Found a 2.6.29 HTC kernel");
                        ins = PerflockActivity.this.getResources().openRawResource(R.raw.perflock_disable29);
                    } else if (moduleName.contains("3215")) {
                        Log.d("perflock_disabler", "Found a 2.6.32.15 HTC kernel");
                        ins = PerflockActivity.this.getResources().openRawResource(R.raw.perflock_disable3215);
                    } else if (moduleName.contains("3217")) {
                        Log.d("perflock_disabler", "Found a 2.6.32.17 HTC kernel");
                        ins = PerflockActivity.this.getResources().openRawResource(R.raw.perflock_disable3217);
                    } else if (moduleName.contains("3221")) {
                        Log.d("perflock_disabler", "Found a 2.6.32.21 HTC kernel");
                        ins = PerflockActivity.this.getResources().openRawResource(R.raw.perflock_disable3221);
                    } else if (moduleName.contains("3510")) {
                        Log.d("perflock_disabler", "Found a 2.6.35.10 HTC kernel");
                        ins = PerflockActivity.this.getResources().openRawResource(R.raw.perflock_disable3510);
                    } else if (moduleName.contains("359")) {
                        Log.d("perflock_disabler", "Found a 2.6.35.9 HTC kernel");
                        ins = PerflockActivity.this.getResources().openRawResource(R.raw.perflock_disable359);
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
                    PerflockActivity.this.chmod(dir.getAbsolutePath() + "/" + moduleName);
                    Log.d("perflock_disabler", "Loading module");
                    PerflockActivity.this.runRootCommand("insmod " + dir.getAbsolutePath() + "/" + moduleName + " perflock_notifier_call_addr=0x" + addr);
                    this.errorMsg = PerflockActivity.this.getResources().getString(R.string.perflock_success);
                    Log.d("perflock_disabler", "Module loaded");
                    this.setCheckBoot = true;
                    new File(dir.getAbsolutePath() + "/" + moduleName).delete();
                    this.handler.sendEmptyMessage(0);
                } else if (this.quit) {
                    Log.d("perflock_disabler", "User cancel");
                    this.errorMsg = PerflockActivity.this.getResources().getString(R.string.perflock_cancel);
                } else {
                    Log.d("perflock_disabler", "Failed to find target address. Perflock is incompatible version or not enabled.");
                    this.errorMsg = PerflockActivity.this.getResources().getString(R.string.perflock_fail);
                }
                this.handler.sendEmptyMessage(0);
            } catch (Exception e) {
                Log.d("perflock_disabler", "Operation aborted. Exception: " + e);
                e.printStackTrace();
                this.errorMsg = PerflockActivity.this.getResources().getString(R.string.perflock_err);
                this.handler.sendEmptyMessage(0);
            }
        }
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(1);
        this.settings = getSharedPreferences("SpeedUp", 0);
        this.editor = this.settings.edit();
        showDialog(false);
    }

    private void showDialog(boolean checkEnabled) {
        View layout = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.root_dialog, null, false);

        TextView textView=(TextView)layout.findViewById(R.id.titleeee);
        textView.setText(getResources().getString(R.string.perflock_disable));

        TextView descTv=(TextView)layout.findViewById(R.id.descriptionDD);
        descTv.setText(getResources().getString(R.string.perflock_disable_warning));




        Builder builder=new Builder(PerflockActivity.this);
        builder.setPositiveButton(getResources().getString(R.string.cont), new C00712());
        AlertDialog dialog=builder.create();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setView(layout);
        dialog.show();
    }

    private void doDisable() {
        Disable disable = new Disable();
        this.pd = ProgressDialog.show(this, getResources().getString(R.string.please_wait), getResources().getString(R.string.perflock_pd), false, true);
        this.pd.setOnCancelListener(new C00734(disable));
        disable.start();
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
