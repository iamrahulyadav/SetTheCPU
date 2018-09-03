package com.ansoft.speedup.profiles.condition;

import android.content.Context;

import com.ansoft.speedup.R;

import java.util.ArrayList;
import java.util.List;

public class LogicAll extends Condition {
    public static final transient int format = 2131492901;
    public static final transient int name = 2131492884;
    private List<Condition> children;

    public LogicAll() {
        this.children = null;
        this.type = "All";
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
        return context.getResources().getString(R.string.condition_all);
    }

    public String getFormattedName(Context context) {
        return context.getResources().getString(R.string.condition_all);
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
            if (!condition.check()) {
                return false;
            }
        }
        return true;
    }
}
