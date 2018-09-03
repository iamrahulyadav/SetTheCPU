package com.ansoft.speedup.profiles;

import android.content.Context;
import com.ansoft.speedup.profiles.condition.Condition;
import java.util.ArrayList;
import java.util.List;

public class Scanner {
    protected ArrayList<Condition> conditions;
    protected Context context;
    protected boolean started;
    protected int updateInterval;
    protected ScannerUpdater updater;

    public interface ScannerUpdater {
        void onScannerUpdate();
    }

    public List<Condition> getConditions() {
        return this.conditions;
    }

    public Scanner(Context context) {
        this.started = false;
        this.conditions = new ArrayList();
        this.context = context;
    }

    public void addCondition(Condition condition) {
        if (!this.conditions.contains(condition)) {
            this.conditions.add(condition);
        }
    }

    public boolean removeCondition(Condition condition) {
        return this.conditions.remove(condition);
    }

    public void setScannerUpdater(ScannerUpdater updater) {
        this.updater = updater;
    }

    public void start() {
        this.started = true;
    }

    public void manualUpdate() {
    }

    public void stop() {
        this.started = false;
    }

    public boolean isAlive() {
        return false;
    }
}
