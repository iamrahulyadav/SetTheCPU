package com.ansoft.speedup.fragment;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.ansoft.speedup.InfoWindowActivity;
import com.ansoft.speedup.Jni;
import com.ansoft.speedup.R;
import com.ansoft.speedup.StresstestActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InfoFragmentList extends Fragment implements PageFragment {
    String[] description;
    ProgressDialog pd;
    boolean stopBench;
    Resources res;
    String[] title;

    class C00981 implements OnClickListener {
        C00981() {
        }

        public void onClick(DialogInterface dialog, int which) {
        }
    }

    class C00992 implements OnClickListener {
        C00992() {
        }

        public void onClick(DialogInterface dialog, int which) {
        }
    }

    private class Benchmark2 implements Runnable {
        double finalTime;
        private Handler f9h;
        int repeat;

        class C01011 extends Handler {
            C01011() {
            }

            public void handleMessage(Message msg) {
                InfoFragmentList.this.pd.dismiss();
                InfoFragmentList.this.benchResults((int) Benchmark2.this.finalTime);
            }
        }

        Benchmark2(int cycles) {
            this.f9h = new C01011();
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
                if (InfoFragmentList.this.stopBench) {
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

        /* renamed from: com.ansoft.setthecpu.fragment.InfoFragmentList.Benchmark.1 */
        class C01001 extends Handler {
            C01001() {
            }

            public void handleMessage(Message msg) {
                if (InfoFragmentList.this.pd != null) {
                    InfoFragmentList.this.pd.dismiss();
                }
                InfoFragmentList.this.benchResults((int) Benchmark.this.finalTime);
            }
        }

        Benchmark(int cycles) {
            this.f10h = new C01001();
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
                            if (InfoFragmentList.this.stopBench) {
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
                if (InfoFragmentList.this.pd != null) {
                    InfoFragmentList.this.pd.dismiss();
                }
                InfoFragmentList.this.nativeResults(NativeBenchmark.this.cTime, NativeBenchmark.this.neonTime);
            }
        }

        NativeBenchmark() {
            this.f12h = new C01021();
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

    class InfoAdapter extends BaseAdapter {


        @Override
        public int getCount() {
            return 5;
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            View cv = getActivity().getLayoutInflater().inflate(R.layout.item_info, null, false);
            ImageView bgImg = (ImageView) cv.findViewById(R.id.bgImage);

            TextView bgTitle = (TextView) cv.findViewById(R.id.bgTitle);


            switch (i){
                case 0:
                    bgImg.setImageResource(R.drawable.ic_devices);
                    bgTitle.setText("Device");
                    break;
                case 1:
                    bgImg.setImageResource(R.drawable.ic_cpu);
                    bgTitle.setText("CPU");
                    break;
                case 2:
                    bgImg.setImageResource(R.drawable.ic_battery);
                    bgTitle.setText("Battery");
                    break;
                case 3:
                    bgImg.setImageResource(R.drawable.ic_clock);
                    bgTitle.setText("Time");
                    break;
                case 4:
                    bgImg.setImageResource(R.drawable.ic_memory);
                    bgTitle.setText("Memory");
                    break;
            }
            return cv;
        }
    }

    public InfoFragmentList() {
        this.stopBench = false;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.res = getResources();
        this.title = this.res.getStringArray(R.array.info_title);
        this.description = this.res.getStringArray(R.array.info_desc);
        View view = inflater.inflate(R.layout.frag_info, container, false);
        ListView listView = (ListView) view.findViewById(R.id.infolistView);
        listView.setAdapter(new InfoAdapter());
        //listView.setAdapter(new SimpleAdapter(getActivity(), getListValues(), android.R.layout.simple_list_item_1, new String[]{"1", "2"}, new int[]{16908308, 16908309}));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (i < 5) {
                    Intent intent = new Intent(getActivity(), InfoWindowActivity.class);
                    intent.putExtra("position", i);
                    startActivity(intent);
                } else if (l == 5) {
                    runBenchmark(1);
                } else if (l == 6) {
                    runBenchmark2(99999999);
                } else if (l == 7) {
                    runNativeBenchmark();
                } else if (l == 8) {
                    startActivity(new Intent(getActivity(), StresstestActivity.class));
                }
            }
        });
        return view;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    private List<Map<String, String>> getListValues() {
        List<Map<String, String>> values = new ArrayList();
        int length = this.title.length;
        for (int i = 0; i < length; i++) {
            Map<String, String> v = new HashMap();
            v.put("1", this.title[i]);
            v.put("2", this.description[i]);
            values.add(v);
        }
        return values;
    }

    private void runBenchmark(int cycles) {
        this.pd = ProgressDialog.show(getActivity(), "Please Wait", "Benchmarking...", true, false);
        Benchmark bench = new Benchmark(cycles);
    }

    private void runBenchmark2(int cycles) {
        this.pd = ProgressDialog.show(getActivity(), "Please Wait", "Benchmarking...", true, false);
        Benchmark2 bench = new Benchmark2(cycles);
    }

    private void runNativeBenchmark() {
        this.pd = ProgressDialog.show(getActivity(), "Please Wait", "Benchmarking...", true, false);
        NativeBenchmark bench = new NativeBenchmark();
    }

    public Integer getTitleRes() {
        return Integer.valueOf(R.string.tab_info);
    }

    public Integer getMenuRes() {
        return null;
    }

    private void nativeResults(String cTime, String neonTime) {
        Log.d("SpeedUp", "Native bench: " + cTime + ", w/NEON: " + neonTime);
        AlertDialog alertDialog = new Builder(getActivity()).create();
        alertDialog.setTitle("Native Benchmark");
        alertDialog.setMessage(cTime + "ms\n\n" + this.res.getString(R.string.nativebenchnote));
        alertDialog.setButton("Ok", new C00981());
        alertDialog.show();
    }

    private void benchResults(int finalTime) {
        Log.d("SpeedUp", "Long benchmark took " + finalTime);
        AlertDialog alertDialog = new Builder(getActivity()).create();
        alertDialog.setTitle(finalTime + "ms");
        alertDialog.setMessage("Benchmark took " + finalTime + "ms.\n" + "Lower is faster.");
        alertDialog.setButton("Ok", new C00992());
        alertDialog.show();
    }

    public void onDetach() {
        super.onDetach();
        this.stopBench = true;
        try {
            if (this.pd != null) {
                this.pd.dismiss();
            }
        } catch (NullPointerException e) {
        }
    }
}
