package com.ansoft.speedup.profiles;

import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ansoft.speedup.AddProfileActivity;
import com.ansoft.speedup.R;
import com.github.paolorotolo.appintro.ISlideBackgroundColorHolder;
import com.google.gson.Gson;
import com.ansoft.speedup.profiles.LogicConditionLayout.OnConditionClickListener;
import com.ansoft.speedup.profiles.condition.Condition;
import com.ansoft.speedup.profiles.condition.ConditionPickerFragment;
import com.ansoft.speedup.profiles.condition.ConditionPickerFragment.OnConditionAddListener;
import com.ansoft.speedup.profiles.conditionconfig.ConditionConfigFragment;
import com.ansoft.speedup.profiles.conditionconfig.ConditionConfigFragment.OnConditionSaveListener;
import java.lang.reflect.InvocationTargetException;

public class ConditionEditFragment extends Fragment  implements ISlideBackgroundColorHolder {
    AddProfileActivity activity;
    Condition baseCondition;
    LogicConditionLayout baseLayout;
    ConditionClickListener conditionClickListener;
    Gson gson;


    @Override
    public int getDefaultBackgroundColor() {
        return getResources().getColor(R.color.white);
    }

    @Override
    public void setBackgroundColor(@ColorInt int backgroundColor) {

    }

    private class ConditionAddListener implements OnConditionAddListener {
        private ConditionAddListener() {
        }

        public void onConditionAdd(Condition parentCondition, Condition addedCondition, View parentView) {
            LogicConditionLayout layout = (LogicConditionLayout) parentView;
            parentCondition.addChild(addedCondition);
            layout.addCondition(addedCondition, false);
        }
    }

    private class ConditionClickListener implements OnConditionClickListener {
        private ConditionClickListener() {
        }

        public void onConditionAdd(Condition parent, View layout) {
            ConditionPickerFragment pickerFragment = new ConditionPickerFragment(parent, layout);
            pickerFragment.setOnConditionAddListener(new ConditionAddListener());
            pickerFragment.show(ConditionEditFragment.this.getFragmentManager().beginTransaction(), "dialog");
        }

        public void onConditionClick(Condition condition, View view) {
            ConditionSaveListener listener = new ConditionSaveListener();
            if (condition.getConfigFragment() != null) {
                Fragment configFragment = null;
                try {
                    configFragment = (ConditionConfigFragment) condition.getConfigFragment().getConstructor(new Class[]{Condition.class, View.class, OnConditionSaveListener.class}).newInstance(new Object[]{condition, view, listener});
                } catch (java.lang.InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                }
                FragmentTransaction ft = ConditionEditFragment.this.getFragmentManager().beginTransaction();
                    ft.add(configFragment, "dialog");
                    ft.commit();
            }
        }

        public void onConditionDelete(Condition condition, View layout) {
            condition.deleteSelf();
            LogicConditionLayout parent = (LogicConditionLayout) layout.getParent();
            parent.removeView(layout);
            parent.getCondition().deleteChild(condition);
        }
    }

    private class ConditionSaveListener implements OnConditionSaveListener {
        private ConditionSaveListener() {
        }

        public void onConditionSave(Condition condition, View view) {
            ((TextView) view.findViewById(R.id.title)).setText(condition.getFormattedName(ConditionEditFragment.this.getActivity()));
        }
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.condition_editor, container, false);
        this.activity = (AddProfileActivity) getActivity();
        this.baseLayout = (LogicConditionLayout) view.findViewById(R.id.baseLayout);
        this.baseCondition = this.activity.getBaseCondition();
        this.conditionClickListener = new ConditionClickListener();
        resetLayout();
        return view;
    }

    private void resetLayout() {
        this.baseLayout.setCondition(this.baseCondition, true, this.conditionClickListener);
        LogicConditionLayout.bindLogicalCondition(this.baseCondition, this.baseLayout, this.conditionClickListener);
    }
}
