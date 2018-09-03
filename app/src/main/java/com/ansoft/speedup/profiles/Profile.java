package com.ansoft.speedup.profiles;

import com.ansoft.speedup.profiles.action.Action;
import com.ansoft.speedup.profiles.action.ActionPerformer;
import com.ansoft.speedup.profiles.condition.Condition;
import com.ansoft.speedup.profiles.condition.Condition.Multimap;
import com.ansoft.speedup.profiles.condition.LogicAll;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Profile implements Comparable<Profile> {
    private List<Action> actions;
    private Condition conditions;
    private boolean enabled;
    private boolean exclusive;
    private String name;
    private int priority;

    public Profile() {
        this.conditions = null;
        this.actions = null;
        this.name = "";
        this.priority = 50;
        this.enabled = true;
        this.exclusive = true;
        if (this.conditions == null) {
            this.conditions = new LogicAll();
        }
        if (this.actions == null) {
            this.actions = new ArrayList();
        }
    }

    public void setBaseCondition(Condition condition) {
        this.conditions = condition;
    }

    public void setActionList(ArrayList<Action> list) {
        this.actions = list;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public int getPriority() {
        return this.priority;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public void setExclusive(boolean exclusive) {
        this.exclusive = exclusive;
    }

    public boolean isExclusive() {
        return this.exclusive;
    }

    public Condition getBaseCondition() {
        return this.conditions;
    }

    public List<Action> getActionList() {
        return this.actions;
    }

    public void registerIntents(Multimap<String, Condition> map) {
        this.conditions.registerIntents(map);
    }

    public void registerScanners(Multimap<Class<? extends Scanner>, Condition> map) {
        this.conditions.registerScanners(map);
    }

    public boolean containsClass(ArrayList<?> objectList, Class<?> cls) {
        Iterator i$ = objectList.iterator();
        while (i$.hasNext()) {
            if (i$.next().getClass().equals(cls)) {
                return false;
            }
        }
        return true;
    }

    public boolean check() {
        return this.conditions.check();
    }

    public boolean performActions(ActionPerformer performer) {
        boolean successful = true;
        for (Action action : this.actions) {
            if (!action.perform(performer)) {
                successful = false;
            }
        }
        return successful;
    }

    public int compareTo(Profile profile) {
        return profile.getPriority() - getPriority();
    }
}
