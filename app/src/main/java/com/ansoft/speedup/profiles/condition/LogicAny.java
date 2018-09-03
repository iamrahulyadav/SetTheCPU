package com.ansoft.speedup.profiles.condition;

import android.content.Context;

import com.ansoft.speedup.R;

import java.util.ArrayList;
import java.util.List;

public class LogicAny extends Condition {
    public static final transient int format = 2131492902;
    public static final transient int name = 2131492885;
    private List<Condition> children;

    public LogicAny() {
        this.children = null;
        this.type = "Any";
        this.category = 0;
        if (this.children == null) {
            this.children = new ArrayList();
            return;
        }
        for (Condition child : this.children) {
            child.setParent(this);
        }
    }

    public String getName(Context context) {
        return context.getResources().getString(R.string.condition_any);
    }

    public String getFormattedName(Context context) {
        return context.getResources().getString(R.string.condition_any);
    }

    public ArrayList<Condition> getChildren() {
        return new ArrayList(this.children);
    }

    public void addChild(Condition child) {
        child.setParent(this);
        this.children.add(child);
    }

    public boolean deleteChild(Condition child) {
        child.setParent(null);
        return this.children.remove(child);
    }

    public boolean check() {
        if (this.children.isEmpty()) {
            return true;
        }
        for (Condition condition : this.children) {
            if (condition.check()) {
                return true;
            }
        }
        return false;
    }
}
