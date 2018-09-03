package com.ansoft.speedup.profiles.condition;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.ansoft.speedup.R;
import com.ansoft.speedup.profiles.condition.Condition.TypeMap;
import com.ansoft.speedup.profiles.conditionconfig.ConditionConfigFragment;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

public class ConditionPickerFragment extends DialogFragment {
    private Collection<Class<? extends Condition>> conditions;
    private OnConditionAddListener listener;
    private Condition parentCondition;
    private View parentView;
    Fragment thisFragment;

    ArrayList<String> strings;
    public ConditionPickerFragment(


    ){

    }

    private class ConditionPickerAdapter extends ArrayAdapter<Class<? extends Condition>> {
        private Context context;
        private LayoutInflater inflater;
        View noneView;
        private ArrayList<Class<? extends Condition>> values;

        /* renamed from: com.ansoft.setthecpu.profiles.condition.ConditionPickerFragment.ConditionPickerAdapter.1 */
        class C01291 implements OnClickListener {
            final /* synthetic */ Class val$condition;

            C01291(Class cls) {
                this.val$condition = cls;
            }

            public void onClick(View v) {
                if (ConditionPickerFragment.this.listener != null) {
                    try {
                        Field configField = this.val$condition.getDeclaredField("config");
                        configField.setAccessible(true);
                        Fragment configFragment = null;
                        try {
                            configFragment = (ConditionConfigFragment) ((Class) configField.get(null)).getConstructor(new Class[]{Condition.class, Condition.class, View.class, OnConditionAddListener.class}).newInstance(new Object[]{ConditionPickerFragment.this.parentCondition, this.val$condition.newInstance(), ConditionPickerFragment.this.parentView, ConditionPickerFragment.this.listener});
                        } catch (java.lang.InstantiationException e) {
                            e.printStackTrace();
                        }
                        getDialog().cancel();
                        FragmentTransaction ft = ConditionPickerFragment.this.getFragmentManager().beginTransaction();
                        ft.add(configFragment, "dialog");
                        ft.commit();
                    } catch (NoSuchFieldException e) {
                        try {
                            ConditionPickerFragment.this.listener.onConditionAdd(ConditionPickerFragment.this.parentCondition, (Condition) this.val$condition.newInstance(), ConditionPickerFragment.this.parentView);
                            getDialog().cancel();
                        } catch (InstantiationException e1) {
                            e1.printStackTrace();
                        } catch (IllegalAccessException e12) {
                            e12.printStackTrace();
                        } catch (java.lang.InstantiationException e1) {
                            e1.printStackTrace();
                        }
                    } catch (IllegalArgumentException e2) {
                        e2.printStackTrace();
                    } catch (IllegalAccessException e3) {
                        e3.printStackTrace();
                    } catch (InstantiationException e4) {
                        e4.printStackTrace();
                    } catch (NoSuchMethodException e5) {
                        e5.printStackTrace();
                    } catch (InvocationTargetException e6) {
                        e6.printStackTrace();
                    }
                }
            }
        }

        public ConditionPickerAdapter(Context context, ArrayList<Class<? extends Condition>> objects) {
            super(context, R.layout.condition_picker_item, objects);
            this.context = context;
            this.values = new ArrayList(objects);
            this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            Iterator i$ = this.values.iterator();
            while (i$.hasNext()) {
                Class<? extends Condition> condition = (Class) i$.next();
                try {
                    if (!((Boolean) condition.getMethod("available", new Class[0]).invoke(condition.newInstance(), new Object[0])).booleanValue()) {
                        objects.remove(condition);
                    }
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e2) {
                    e2.printStackTrace();
                } catch (InvocationTargetException e3) {
                    e3.printStackTrace();
                } catch (NoSuchMethodException e4) {
                    e4.printStackTrace();
                } catch (InstantiationException e5) {
                    e5.printStackTrace();
                } catch (java.lang.InstantiationException e) {
                    e.printStackTrace();
                }
            }
            this.values = new ArrayList(objects);
            this.noneView = new View(context);
            this.noneView.setVisibility(View.GONE);
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            Class<? extends Condition> condition = (Class) this.values.get(position);
            convertView = this.inflater.inflate(R.layout.condition_picker_item, null);

            convertView.setOnClickListener(new C01291(condition));
            TextView name = (TextView) convertView.findViewById(R.id.name);

            name.setText(getNameFromPackage(condition.getName()));
            return convertView;
        }
    }

    public interface OnConditionAddListener {
        void onConditionAdd(Condition condition, Condition condition2, View view);
    }

    @SuppressLint("ValidFragment")
    public ConditionPickerFragment(Condition parentCondition, View parentView) {
        this.conditions = TypeMap.values();
        this.parentCondition = parentCondition;
        this.parentView = parentView;
        this.thisFragment = this;
        setRetainInstance(true);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        strings=new ArrayList<>();
        strings.add("Applications");
        strings.add("Battery Level");
        strings.add("Battery Temperature");
        strings.add("Charge Level");
        strings.add("CPU Temperature");
        strings.add("Screen Settings");
        strings.add("Screen Unlocked");
        strings.add("Call");
        strings.add("Time Range");
        strings.add("Day Of Week");
        strings.add("All");
        strings.add("Any");
        strings.add("None");
        View view = inflater.inflate(R.layout.condition_picker, container, false);
        ((ListView) view.findViewById(R.id.layout)).setAdapter(new ConditionPickerAdapter(getActivity(), new ArrayList(this.conditions)));
        //getDialog().setTitle(getActivity().getString(R.string.add_condition));

        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        return view;
    }

    public static String getStaticPckg(Class tClass){
        return tClass.getName();
    }

    public String getNameFromPackage(String packageName){
        if (packageName.equals(getStaticPckg(AppState.class))) {
            return strings.get(0);

        } else if (packageName.equals(getStaticPckg(BatteryLevelState.class))) {
            return strings.get(1);

        } else if (packageName.equals(getStaticPckg(BatteryTempState.class))) {
            return strings.get(2);

        } else if (packageName.equals(getStaticPckg(ChargeState.class))) {
            return strings.get(3);

        } else if (packageName.equals(getStaticPckg(CpuTempState.class))) {
            return strings.get(4);

        } else if (packageName.equals(getStaticPckg(ScreenState.class))) {
            return strings.get(5);

        } else if (packageName.equals(getStaticPckg(UserPresentState.class))) {
            return strings.get(6);

        } else if (packageName.equals(getStaticPckg(CallState.class))) {
            return strings.get(7);

        } else if (packageName.equals(getStaticPckg(TimeRangeState.class))) {
            return strings.get(8);

        } else if (packageName.equals(getStaticPckg(DayOfWeekState.class))) {
            return strings.get(9);

        } else if (packageName.equals(getStaticPckg(LogicAll.class))) {
            return strings.get(10);

        } else if (packageName.equals(getStaticPckg(LogicAny.class))) {
            return strings.get(11);

        } else if (packageName.equals(getStaticPckg(LogicNone.class))) {
            return strings.get(12);

        }
        return null;
    }

    public void setOnConditionAddListener(OnConditionAddListener listener) {
        this.listener = listener;
    }
}
