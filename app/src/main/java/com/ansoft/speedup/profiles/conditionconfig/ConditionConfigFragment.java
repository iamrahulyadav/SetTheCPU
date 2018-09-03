package com.ansoft.speedup.profiles.conditionconfig;

import android.annotation.SuppressLint;
import android.support.v4.app.Fragment;
import android.view.View;
import com.ansoft.speedup.profiles.condition.Condition;
import com.ansoft.speedup.profiles.condition.ConditionPickerFragment.OnConditionAddListener;

public class ConditionConfigFragment extends Fragment {
    protected OnConditionAddListener addListener;
    protected Condition condition;
    protected Condition parentCondition;
    protected View parentView;
    protected OnConditionSaveListener saveListener;
    protected View view;

    public ConditionConfigFragment(){

    }

    public class ConditionConfigMismatchException extends Exception {
    }

    public interface OnConditionSaveListener {
        void onConditionSave(Condition condition, View view);
    }

    @SuppressLint("ValidFragment")
    public ConditionConfigFragment(Condition parentCondition, Condition condition, View parentView, OnConditionAddListener listener) {
        this.condition = condition;
        this.parentCondition = parentCondition;
        this.parentView = parentView;
        this.addListener = listener;
        setRetainInstance(true);
    }

    @SuppressLint("ValidFragment")
    public ConditionConfigFragment(Condition condition, View view, OnConditionSaveListener listener) {
        this.view = view;
        this.condition = condition;
        this.saveListener = listener;
        setRetainInstance(true);
    }

    protected void saveCondition() {
        if (this.addListener != null) {
            this.addListener.onConditionAdd(this.parentCondition, this.condition, this.parentView);
        } else if (this.saveListener != null) {
            this.saveListener.onConditionSave(this.condition, this.view);
        }
    }

    public Condition getCondition() {
        return this.condition;
    }

    protected void onPositiveClick() {
    }
}
