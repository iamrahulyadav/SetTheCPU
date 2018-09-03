package com.ansoft.speedup.profiles;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;

import com.ansoft.speedup.AddProfileActivity;
import com.ansoft.speedup.R;
import com.ansoft.speedup.profiles.condition.Condition;
import com.ansoft.speedup.util.ClickEffect;
import com.ansoft.speedup.util.HintAlertDialog;
import com.ansoft.speedup.widget.PrioritySliderContainer;
import com.ansoft.speedup.widget.PrioritySliderContainer.OnFreqChangeListener;
import java.util.ArrayList;
import java.util.Iterator;

public class ProfileSettingsFragment extends Fragment implements OnFreqChangeListener {
    private AddProfileActivity activity;
    private CheckBox checkboxExclusive;
    private PrioritySliderContainer prioritySlider;
    private Profile profile;
    private EditText profileName;

    /* renamed from: com.ansoft.setthecpu.profiles.ProfileSettingsFragment.1 */
    class C01261 implements OnCheckedChangeListener {
        C01261() {
        }

        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            ProfileSettingsFragment.this.profileName.clearFocus();
            ProfileSettingsFragment.this.profile.setExclusive(isChecked);

        }
    }

    /* renamed from: com.ansoft.setthecpu.profiles.ProfileSettingsFragment.2 */
    class C01272 implements TextWatcher {
        C01272() {
        }

        public void afterTextChanged(Editable s) {
            if (s.length() <= 0) {
                ProfileSettingsFragment.this.profile.setName((String) ProfileSettingsFragment.this.profileName.getHint());
            } else {
                ProfileSettingsFragment.this.profile.setName(s.toString());
            }
        }

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }
    }

    private class FocusListener implements OnFocusChangeListener {
        private FocusListener() {
        }

        public void onFocusChange(View v, boolean hasFocus) {
            if (!hasFocus) {
                ((InputMethodManager) ProfileSettingsFragment.this.activity.getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(ProfileSettingsFragment.this.profileName.getWindowToken(), 0);
            }
        }
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.profile_settings_editor, null);
        this.activity = (AddProfileActivity) getActivity();
        FocusListener listener = new FocusListener();
        this.profile = this.activity.getProfile();
        ArrayList<Integer> exclusive = this.activity.getExclusivePriorities();
        Integer defaultPriority = Integer.valueOf(this.profile.getPriority());
        if (exclusive != null) {
            boolean movingDown = true;
            while (exclusive.contains(defaultPriority)) {
                if (movingDown) {
                    defaultPriority = Integer.valueOf(defaultPriority.intValue() - 1);
                    if (defaultPriority.intValue() < 0) {
                        defaultPriority = Integer.valueOf(50);
                        movingDown = false;
                    }
                } else {
                    defaultPriority = Integer.valueOf(defaultPriority.intValue() + 1);
                    if (defaultPriority.intValue() > 100) {
                        defaultPriority = Integer.valueOf(50);
                        break;
                    }
                }
            }
        }

        new ClickEffect().Opacity(view.findViewById(R.id.priorityHint));
        new ClickEffect().Opacity(view.findViewById(R.id.exclusiveHint));
        view.findViewById(R.id.priorityHint).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showHInt(Hints.PRIOIRTY);
            }
        });

        view.findViewById(R.id.exclusiveHint).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showHInt(Hints.EXCLUSIVE);
            }
        });
        this.profileName = (EditText) view.findViewById(R.id.profileName);
        this.prioritySlider = (PrioritySliderContainer) view.findViewById(R.id.container);
        this.prioritySlider.setOnFreqChangeListener(this);
        this.prioritySlider.init(exclusive, defaultPriority.intValue(), "", this.activity.getResources().getString(R.string.priority));
        this.checkboxExclusive = (CheckBox) view.findViewById(R.id.checkbox_exclusive);
        this.checkboxExclusive.setOnCheckedChangeListener(new C01261());
        if (!this.profile.isExclusive()) {
            this.checkboxExclusive.setChecked(false);
        }
        ArrayList<Condition> conditions = this.profile.getBaseCondition().getChildren();
        if (conditions == null || conditions.isEmpty()) {
            this.profileName.setHint("My Preset");
        } else {
            boolean hasLogic = false;
            Iterator i$ = conditions.iterator();
            while (i$.hasNext()) {
                Condition c = (Condition) i$.next();
                if (c.getCategory() != 0) {
                    hasLogic = true;
                    this.profileName.setHint(c.getFormattedName(getActivity()));
                    break;
                }
            }
            if (!hasLogic) {
                this.profileName.setHint(((Condition) conditions.get(0)).getFormattedName(getActivity()));
            }
        }
        if (!((this.profile.getName() == "" && this.profile.getName() == null) || this.profileName.getHint().equals(this.profile.getName()))) {
            this.profileName.setText(this.profile.getName());
        }
        this.profileName.addTextChangedListener(new C01272());
        if (this.profile.getName().equals(this.profileName.getHint())) {
            this.profileName.setText("");
        }
        this.profileName.setOnFocusChangeListener(listener);
        return view;
    }

    public enum Hints{PRIOIRTY, EXCLUSIVE}

    public void showHInt(Hints hint){
        switch (hint){
            case PRIOIRTY:
                new HintAlertDialog(getActivity(), "Priority", getResources().getText(R.string.profile_priority_description)+"").show();
                break;

            case EXCLUSIVE:
                new HintAlertDialog(getActivity(), "Exclusive", getResources().getText(R.string.profile_exclusive_description)+"").show();
                break;
        }
    }

    public void save() {
        if (this.profile.getName().equals("") || this.profile.getName() == null) {
            this.profile.setName((String) this.profileName.getHint());
        }
        this.profile.setPriority(this.prioritySlider.getMax());
        this.profile.setExclusive(this.checkboxExclusive.isChecked());
    }

    public void onFreqChange(int max) {
        this.profile.setPriority(max);
    }
}
