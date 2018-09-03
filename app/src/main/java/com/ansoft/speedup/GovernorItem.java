package com.ansoft.speedup;

import com.ansoft.speedup.util.Cpufreq;

public class GovernorItem {
    private Cpufreq cpufreq;
    private String name;
    private String text;

    public GovernorItem(String name, String text, Cpufreq cpufreq) {
        this.name = name;
        this.text = text;
        this.cpufreq = cpufreq;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getText() {
        return this.text;
    }

    public String getName() {
        return this.name;
    }

    public String getReadableName() {
        return this.name.replaceAll("_", " ");
    }

    public void setCurrent(long value) {
        this.cpufreq.setGovernorParam(this.name, value);
    }

    public long getCurrent() {
        return this.cpufreq.getGovernorParam(this.name);
    }
}
