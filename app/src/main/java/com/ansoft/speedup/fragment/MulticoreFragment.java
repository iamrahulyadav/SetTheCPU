package com.ansoft.speedup.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

import com.ansoft.speedup.MainActivity;
import com.ansoft.speedup.R;
import com.ansoft.speedup.util.Cpufreq;
import java.util.List;

public class MulticoreFragment extends Fragment implements PageFragment {
    private Activity mActivity;
    List<Button> mCoreButtons;
    private Cpufreq mCpufreq;
    private int mNumCores;

    class Core {
        private Button mControlButton;
        private Button mLockButton;
        private Button mToggleButton;

        class Clicker implements OnClickListener {
            Clicker() {
            }

            public void onClick(View view) {
                if (!view.equals(Core.this.mToggleButton) && !view.equals(Core.this.mLockButton) && view.equals(Core.this.mControlButton)) {
                }
            }
        }

        Core() {
        }
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.multicore, null);
        this.mActivity = getActivity();
        this.mCpufreq = ((MainActivity) this.mActivity).getCpufreq();
        this.mNumCores = this.mCpufreq.getNumCores();
        if (this.mNumCores > 4) {
            this.mNumCores = 4;
        }
        Button core0 = (Button) view.findViewById(R.id.core0);
        Button core1 = (Button) view.findViewById(R.id.core1);
        Button core2 = (Button) view.findViewById(R.id.core2);
        Button core3 = (Button) view.findViewById(R.id.core3);
        int i = this.mNumCores;
        return view;
    }

    public Integer getTitleRes() {
        return Integer.valueOf(R.string.tab_multicore);
    }

    public Integer getMenuRes() {
        return null;
    }
}
