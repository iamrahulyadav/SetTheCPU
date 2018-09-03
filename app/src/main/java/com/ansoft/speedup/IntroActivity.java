package com.ansoft.speedup;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;

import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntroFragment;

import java.io.DataOutputStream;
import java.io.IOException;

public class IntroActivity extends AppIntro {
    public static final int version = 82;
    Editor editor;
    Process process;
    SharedPreferences settings;

    private static final int INTERNET=1;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addSlide(AppIntroFragment.newInstance("Welcome", "Everything you need to do after rooting your device",
                R.drawable.appintrobg4, Color.parseColor("#2c2b27")));
        addSlide(AppIntroFragment.newInstance("Intelligent Presets", "You can choose from a number of condition to define your presets",
                R.drawable.appintrobg2, getResources().getColor(R.color.colorFAB6)));
        addSlide(AppIntroFragment.newInstance("Root Permission", "The app requires root permission to function, so please allow root permission on next dialog",
                R.drawable.appintrobg3, getResources().getColor(R.color.colorFAB7)));
        setSeparatorColor(getResources().getColor(android.R.color.transparent));
        showSkipButton(false);
        setDepthAnimation();
        setProgressButtonEnabled(true);
        setBarColor(getResources().getColor(android.R.color.transparent));
        setSeparatorColor(getResources().getColor(android.R.color.transparent));
        showSkipButton(false);
        showDoneButton(true);
        setVibrate(true);
        setVibrateIntensity(30);
        this.settings = getSharedPreferences("SpeedUp", 0);
        this.editor = this.settings.edit();

        if (Build.VERSION.SDK_INT>=23){
            ActivityCompat.requestPermissions(IntroActivity.this,
                    new String[]{   android.Manifest.permission.INTERNET,
                            android.Manifest.permission.ACCESS_WIFI_STATE,
                            Manifest.permission.ACCESS_NETWORK_STATE,
                            Manifest.permission.WAKE_LOCK,
                            Manifest.permission.VIBRATE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_PHONE_STATE,
                            Manifest.permission.READ_LOGS,
                            Manifest.permission.RECEIVE_BOOT_COMPLETED
                    },
                    INTERNET);
        }
    }


    public void exit(){
        toastMessage("This permission is needed");
        finish();
    }


    public void toastMessage(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case INTERNET: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                } else {
                    exit();
                }
                return;
            }

        }
    }

    private void storeConstants(String device) {
        this.editor.putString("device", device);
        this.editor.commit();
    }

    private void launchMainActivity() {
        try {
            this.process = Runtime.getRuntime().exec("su");
            new DataOutputStream(this.process.getOutputStream()).writeBytes("exit\n");
            this.process.waitFor();
        } catch (IOException e) {
        } catch (InterruptedException e2) {
        }
        Intent i = new Intent(this, MainActivity.class);
        i.putExtra("hasRoot", true);
        startActivity(i);
        finish();
    }


    private void storeVersion(int version) {
        this.settings = getSharedPreferences("SpeedUp", 0);
        Editor editor = this.settings.edit();
        editor.putInt("androidVersion", version);
        editor.commit();
    }

    @Override
    public void onDonePressed() {
        super.onDonePressed();

        IntroActivity.this.storeVersion(IntroActivity.version);
        IntroActivity.this.storeConstants("autodetect");
        IntroActivity.this.launchMainActivity();
    }
}
