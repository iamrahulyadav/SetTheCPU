package com.ansoft.speedup;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;

import com.ansoft.speedup.fragment.GovernorFragment;
import com.ansoft.speedup.fragment.HomeFragment;
import com.ansoft.speedup.fragment.InfoFragmentList;
import com.ansoft.speedup.fragment.PageFragment;
import com.ansoft.speedup.fragment.ProfilesFragment;
import com.ansoft.speedup.sysfs.VoltageTable;
import com.ansoft.speedup.util.Cpufreq;
import com.ansoft.speedup.util.HintAlertDialog;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    Cpufreq cpufreq = null;
    SharedPreferences.Editor editor;
    boolean hasRoot = true;
    Context mContext;

    boolean stopBench;
    ViewPager viewPager;
    List<PageFragment> mFragments;
    private boolean mShowVoltages;
    SharedPreferences settings;

    ProgressDialog pd;
    InterstitialAd mInterstitialAd;
    private static Animation fab_open, fab_close, rotate_forward, rotate_backward;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences  sharedPreferences = getSharedPreferences(FirstTime.FLAG, Context.MODE_PRIVATE);

        if(sharedPreferences.getBoolean(FirstTime.FLAG,true)){


            startActivity(new Intent(MainActivity.this,IntroActivity.class));

            SharedPreferences.Editor e=sharedPreferences.edit();

            e.putBoolean(FirstTime.FLAG,false);

            e.apply();
            finish();
        }else {


            fab_open = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open);
            fab_close = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_close);
            rotate_forward = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_forward);
            rotate_backward = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_backward);

            setContentView(R.layout.activity_main);
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                    this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            drawer.setDrawerListener(toggle);
            toggle.syncState();

            NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
            navigationView.setNavigationItemSelectedListener(this);

            TabLayout tabLayout = (TabLayout) findViewById(R.id.tabLayout);
            viewPager = (ViewPager) findViewById(R.id.viewPager);


            final AdView mAdView = (AdView) findViewById(R.id.adView);
            final AdRequest adRequest = new AdRequest.Builder()
                    .addTestDevice("D66054767372CF1F16E35ABDBCF25CE6")
                    .build();

            mAdView.loadAd(adRequest);
            mAdView.setAdListener(new AdListener() {
                @Override
                public void onAdLoaded() {
                    mAdView.setVisibility(View.VISIBLE);
                    mAdView.loadAd(adRequest);
                    super.onAdLoaded();
                }

                @Override
                public void onAdClosed() {
                    super.onAdClosed();
                }

                @Override
                public void onAdFailedToLoad(int i) {
                    super.onAdFailedToLoad(i);
                }

                @Override
                public void onAdLeftApplication() {
                    super.onAdLeftApplication();
                }

                @Override
                public void onAdOpened() {
                    super.onAdOpened();
                }
            });
            settings = getSharedPreferences("SpeedUp", 0);
            boolean voltages = true;
            try {
                Process process = Runtime.getRuntime().exec("su");
                new DataOutputStream(process.getOutputStream()).writeBytes("exit\n");
                process.waitFor();
                if (process.exitValue() != 0) {
                    hasRoot = false;
                }
            } catch (IOException e) {
                hasRoot = false;
            } catch (InterruptedException e2) {
                hasRoot = false;
            }
            if (!new File(VoltageTable.PATH).exists()) {
                voltages = false;
            }
            mShowVoltages = voltages;
            SectionPagerAdapter adapter = new SectionPagerAdapter(getSupportFragmentManager());
            adapter.add(new HomeFragment(), "Home");
            adapter.add(new ProfilesFragment(), "Presets");
            //adapter.add(new GovernorFragment(), "Governer");
            adapter.add(new InfoFragmentList(), "Info");
            viewPager.setAdapter(adapter);
            tabLayout.setupWithViewPager(viewPager);
            editor = settings.edit();

            mInterstitialAd = new InterstitialAd(this);
            mInterstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712");

            mInterstitialAd.setAdListener(new AdListener() {
                @Override
                public void onAdClosed() {
                    requestNewInterstitial();
                }
            });
            requestNewInterstitial();
        }
    }

    private void requestNewInterstitial() {
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice("D66054767372CF1F16E35ABDBCF25CE6")
                .build();

        mInterstitialAd.loadAd(adRequest);
    }

    public SharedPreferences getSettings(){
        return this.settings;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        }
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.nav_integer_bench) {
            // Handle the camera action
            runBenchmark(1);
        } else if (id == R.id.nav_floating_bench) {
            runBenchmark2(99999999);
        } else if (id == R.id.nav_stress_test) {
            startActivity(new Intent(MainActivity.this, StresstestActivity.class));
        } else if (id == R.id.nav_about_me) {
            String title="About Me";
            String summary="Developed By \nAbinash Neupane\nabinash.neupane123@gmail.com\nabinashneupane.wordpress.com\n+9779816671050";
            HintAlertDialog alertDialog=new HintAlertDialog(MainActivity.this, title, summary);
            alertDialog.show();
        }else if (id == R.id.nav_open_sources) {
            String title="Copyright and Licenses";
            String summary=getResources().getText(R.string.copyright)+"";
            HintAlertDialog alertDialog=new HintAlertDialog(MainActivity.this, title, summary);
            alertDialog.show();
        }else if (id==R.id.remove_ad){
            final String appPackageName = "com.ansoft.speedup"; // getPackageName() from Context or Activity object
            try {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
            }
            catch (android.content.ActivityNotFoundException anfe) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + appPackageName)));
            }
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        return true;
    }

    class SectionPagerAdapter extends FragmentPagerAdapter {

        List<Fragment> fragments;
        List<String> titles;

        public SectionPagerAdapter(FragmentManager fm) {
            super(fm);
            fragments = new ArrayList<>();
            titles = new ArrayList<>();
        }

        public void add(Fragment fragment, String title) {
            fragments.add(fragment);
            titles.add(title);
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titles.get(position);
        }
    }

    public boolean getHasRoot() {
        return hasRoot;
    }

    public Cpufreq getCpufreq() {
        if (cpufreq == null) {
            cpufreq = new Cpufreq(this, true);
        }
        return cpufreq;
    }

    protected void onDestroy() {
        super.onDestroy();
        if (cpufreq != null) {
            cpufreq.kill();
        }
    }

    private int getVersion() {
        return settings.getInt("androidVersion", -1);
    }

    public static boolean animateFAB(boolean isFabOpen, FloatingActionButton mainFab, ArrayList<FloatingActionButton> fabs) {
        boolean open;
        if (isFabOpen) {
            mainFab.startAnimation(rotate_backward);
            for (FloatingActionButton fab : fabs) {
                fab.startAnimation(fab_close);
                fab.setClickable(false);
            }
            open = false;
        } else {
            mainFab.startAnimation(rotate_forward);
            for (FloatingActionButton fab : fabs) {
                fab.startAnimation(fab_open);
                fab.setClickable(true);
            }
            open = true;
        }
        return open;

    }

    public static boolean animateFrame(boolean isFabOpen, FloatingActionButton mainFab, ArrayList<FrameLayout> fabs) {
        boolean open;
        if (isFabOpen) {
            mainFab.startAnimation(rotate_backward);
            for (FrameLayout fab : fabs) {
                fab.startAnimation(fab_close);
                fab.setClickable(false);
            }
            open = false;
        } else {
            mainFab.startAnimation(rotate_forward);
            for (FrameLayout fab : fabs) {
                fab.startAnimation(fab_open);
                fab.setClickable(true);
            }
            open = true;
        }
        return open;

    }


    private class Benchmark2 implements Runnable {
        double finalTime;
        private Handler f9h;
        int repeat;

        class C01011 extends Handler {
            C01011() {
            }

            public void handleMessage(Message msg) {
                pd.dismiss();
                benchResults((int) Benchmark2.this.finalTime);
            }
        }

        Benchmark2(int cycles) {
            this.f9h = new Benchmark2.C01011();
            this.repeat = cycles;
            new Thread(this).start();
        }

        public void run() {
            int k;
            for (k = 0; k < this.repeat; k++) {
                Math.sqrt(3.141592653589793d);
            }
            long currentTime = System.currentTimeMillis();
            for (k = 0; k < this.repeat; k++) {
                Math.sqrt(3.141592653589793d);
                if (stopBench) {
                    break;
                }
            }
            this.finalTime = (double) (System.currentTimeMillis() - currentTime);
            this.f9h.sendEmptyMessage(0);
        }
    }

    private class Benchmark implements Runnable {
        double finalTime;
        private Handler f10h;
        long f11n;
        int repeat;

        class C01001 extends Handler {
            C01001() {
            }

            public void handleMessage(Message msg) {
                if (pd != null) {
                    pd.dismiss();
                }
                benchResults((int) Benchmark.this.finalTime);
            }
        }

        Benchmark(int cycles) {
            this.f10h = new Benchmark.C01001();
            this.repeat = cycles;
            new Thread(this).start();
        }

        public void run() {
            long currentTime = System.currentTimeMillis();
            this.f11n = 285045041;
            for (int k = 0; k < this.repeat; k++) {
                for (int j = 0; j <= 10; j++) {
                    for (long i = 2; i <= this.f11n / i; i++) {
                        while (this.f11n % i == 0) {
                            this.f11n /= i;
                            if (stopBench) {
                                break;
                            }
                        }
                    }
                }
            }
            this.finalTime = (double) (System.currentTimeMillis() - currentTime);
            this.f10h.sendEmptyMessage(0);
        }
    }

    private class NativeBenchmark implements Runnable {
        String cTime;
        private Handler f12h;
        String neonTime;

        /* renamed from: com.ansoft.setthecpu.fragment.InfoFragmentList.NativeBenchmark.1 */
        class C01021 extends Handler {
            C01021() {
            }

            public void handleMessage(Message msg) {
                if (pd != null) {
                    pd.dismiss();
                }
                nativeResults(NativeBenchmark.this.cTime, NativeBenchmark.this.neonTime);
            }
        }

        NativeBenchmark() {
            this.f12h = new NativeBenchmark.C01021();
            new Thread(this).start();
        }

        public void run() {
            this.cTime = Jni.cbench();
            this.neonTime = Jni.neonbench();
            if (Double.parseDouble(this.neonTime) == -1.0d) {
                this.neonTime = "N/A";
            }
            this.f12h.sendEmptyMessage(0);
        }
    }


    private void runBenchmark(int cycles) {
        this.pd = ProgressDialog.show(MainActivity.this, "Please Wait", "Benchmarking...", true, false);
        Benchmark bench = new Benchmark(cycles);
    }

    private void runBenchmark2(int cycles) {
        this.pd = ProgressDialog.show(MainActivity.this, "Please Wait", "Benchmarking...", true, false);
        Benchmark2 bench = new Benchmark2(cycles);
    }

    private void runNativeBenchmark() {
        this.pd = ProgressDialog.show(MainActivity.this, "Please Wait", "Benchmarking...", true, false);
        NativeBenchmark bench = new NativeBenchmark();
    }

    private void nativeResults(String cTime, String neonTime) {
        Log.d("SpeedUp", "Native bench: " + cTime + ", w/NEON: " + neonTime);
        AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
        alertDialog.setTitle("Native Benchmark");
        alertDialog.setMessage(cTime + "ms\n\n" + getResources().getString(R.string.nativebenchnote));
        alertDialog.setButton("Ok", new C00981());
        alertDialog.show();
    }

    private void benchResults(int finalTime) {
        Log.d("SpeedUp", "Long benchmark took " + finalTime);
        AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
        alertDialog.setTitle(finalTime + "ms");
        alertDialog.setMessage("Benchmark took " + finalTime + "ms.\n" + "Lower is faster.");
        alertDialog.setButton("Ok", new C00981());
        alertDialog.show();
    }

    class C00981 implements DialogInterface.OnClickListener {
        C00981() {
        }

        public void onClick(DialogInterface dialog, int which) {
        }
    }
}
