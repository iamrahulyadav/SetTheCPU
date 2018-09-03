package com.ansoft.speedup;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.SystemClock;
import android.util.Log;

public class StresstestActivity extends Activity {
    FloatBench floatBench0;
    FloatBench floatBench1;
    FloatBench floatBench2;
    FloatBench floatBench3;
    IntBench intBench0;
    IntBench intBench1;
    IntBench intBench2;
    IntBench intBench3;
    ProgressDialog pd;
    boolean run;
    long time;
    boolean trigger;
    WakeLock wl;

    /* renamed from: com.ansoft.setthecpu.StresstestActivity.1 */
    class C00771 implements OnCancelListener {
        C00771() {
        }

        public void onCancel(DialogInterface arg0) {
            StresstestActivity.this.run = false;
            if (StresstestActivity.this.wl.isHeld()) {
                StresstestActivity.this.wl.release();
            }
        }
    }

    /* renamed from: com.ansoft.setthecpu.StresstestActivity.2 */
    class C00782 implements OnClickListener {
        C00782() {
        }

        public void onClick(DialogInterface dialog, int which) {
            if (StresstestActivity.this.wl.isHeld()) {
                StresstestActivity.this.wl.release();
            }
            StresstestActivity.this.finish();
        }
    }

    /* renamed from: com.ansoft.setthecpu.StresstestActivity.3 */
    class C00793 implements OnClickListener {
        C00793() {
        }

        public void onClick(DialogInterface dialog, int which) {
            if (StresstestActivity.this.wl.isHeld()) {
                StresstestActivity.this.wl.release();
            }
            StresstestActivity.this.finish();
        }
    }

    private class FloatBench implements Runnable {
        boolean fail;
        private Handler handler;
        int f0i;
        int threadId;

        /* renamed from: com.ansoft.setthecpu.StresstestActivity.FloatBench.1 */
        class C00801 extends Handler {
            C00801() {
            }

            public void handleMessage(Message msg) {
                if (!StresstestActivity.this.trigger) {
                    if (StresstestActivity.this.pd.isShowing()) {
                        try {
                            StresstestActivity.this.pd.dismiss();
                        } catch (Exception e) {
                            Log.d("SpeedUp", "Stress test aborted.");
                        }
                    }
                    if (StresstestActivity.this.wl.isHeld()) {
                        StresstestActivity.this.wl.release();
                    }
                    StresstestActivity.this.trigger = true;
                    if (FloatBench.this.fail) {
                        StresstestActivity.this.benchFail(FloatBench.this.threadId);
                    } else {
                        StresstestActivity.this.benchSucceed();
                    }
                }
            }
        }

        FloatBench(int t) {
            this.handler = new C00801();
            this.f0i = 0;
            this.threadId = t;
            StresstestActivity.this.run = true;
            this.fail = false;
            StresstestActivity.this.trigger = false;
            new Thread(this).start();
        }

        public void run() {
            double sqrt = Math.sqrt(3.141592653589793d);
            while (StresstestActivity.this.run) {
                this.f0i++;
                if (Math.sqrt(3.141592653589793d) != sqrt) {
                    this.fail = true;
                    StresstestActivity.this.run = false;
                    break;
                }
            }
            this.handler.sendEmptyMessage(0);
        }
    }

    private class IntBench implements Runnable {
        boolean fail;
        private Handler handler;
        int f1i;
        int threadId;

        /* renamed from: com.ansoft.setthecpu.StresstestActivity.IntBench.1 */
        class C00811 extends Handler {
            C00811() {
            }

            public void handleMessage(Message msg) {
                if (!StresstestActivity.this.trigger) {
                    if (StresstestActivity.this.pd.isShowing()) {
                        StresstestActivity.this.pd.dismiss();
                    }
                    if (StresstestActivity.this.wl.isHeld()) {
                        StresstestActivity.this.wl.release();
                    }
                    StresstestActivity.this.trigger = true;
                    if (IntBench.this.fail) {
                        StresstestActivity.this.benchFail(IntBench.this.threadId);
                    } else {
                        StresstestActivity.this.benchSucceed();
                    }
                }
            }
        }

        IntBench(int t) {
            this.handler = new C00811();
            this.f1i = 0;
            this.threadId = t;
            StresstestActivity.this.run = true;
            this.fail = false;
            StresstestActivity.this.trigger = false;
            new Thread(this).start();
        }

        public void run() {
            while (StresstestActivity.this.run) {
                this.f1i++;
                if (1048576 != 1048576) {
                    this.fail = true;
                    StresstestActivity.this.run = false;
                    break;
                }
            }
            this.handler.sendEmptyMessage(0);
        }
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(1);
        this.wl = ((PowerManager) getSystemService(Context.POWER_SERVICE)).newWakeLock(6, "Stress Test WakeLock");
        this.wl.acquire();
        runBenchmark();
    }

    private void runBenchmark() {
        OnCancelListener listener = new C00771();
        this.pd = ProgressDialog.show(this, getResources().getString(R.string.stress_test_header), getResources().getString(R.string.stress_test_note), true, true, listener);
        this.time = System.currentTimeMillis();
        SystemClock.sleep(1000);
        this.floatBench0 = new FloatBench(0);
        this.floatBench1 = new FloatBench(1);
        this.floatBench2 = new FloatBench(2);
        this.floatBench3 = new FloatBench(3);
        this.intBench0 = new IntBench(0);
        this.intBench1 = new IntBench(1);
        this.intBench2 = new IntBench(2);
        this.intBench3 = new IntBench(3);
    }

    private void benchFail(int t) {
        this.run = false;
        Log.e("SpeedUp", "Arithmetic error on thread " + t + ". Lasted for " + (System.currentTimeMillis() - this.time) + " ms");
        AlertDialog alertDialog = new Builder(this).create();
        alertDialog.setTitle("Stress test failed!");
        alertDialog.setMessage("Halted: arithmetic error on thread " + t + ". Lasted for " + (System.currentTimeMillis() - this.time) + " ms");
        alertDialog.setButton("Ok", new C00782());
        alertDialog.show();
    }

    private void benchSucceed() {
        this.run = false;
        Log.d("SpeedUp", "Stress test lasted for " + (System.currentTimeMillis() - this.time) + " ms with no errors.");
        AlertDialog alertDialog = new Builder(this).create();
        alertDialog.setTitle("Stress test results");
        alertDialog.setMessage("Stress test lasted for " + (System.currentTimeMillis() - this.time) + " ms with no errors.");
        alertDialog.setButton("Ok", new C00793());
        alertDialog.show();
    }

    public void onStop() {
        super.onStop();
        this.run = false;
        if (this.wl.isHeld()) {
            this.wl.release();
        }
        finish();
    }

    public void onPause() {
        super.onPause();
        this.run = false;
        if (this.wl.isHeld()) {
            this.wl.release();
        }
        finish();
    }

    public void onDestroy() {
        super.onDestroy();
        this.run = false;
        if (this.wl.isHeld()) {
            finish();
        }
    }
}
