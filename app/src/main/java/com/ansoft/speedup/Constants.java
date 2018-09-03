package com.ansoft.speedup;

public class Constants {
    int defaultMax;
    final int defaultMax_nvidia_tegra2;
    int defaultMin;
    final int defaultMin_nvidia_tegra2;
    Integer[] freq;
    String[] freqText;
    final String[] freqText_nvidia_tegra2;
    final Integer[] freq_nvidia_tegra2;
    int limitMax;
    final int limitMax_nvidia_tegra2;
    int limitMin;
    final int limitMin_nvidia_tegra2;

    public Constants(String config) {
        this.freq_nvidia_tegra2 = new Integer[]{Integer.valueOf(216000), Integer.valueOf(312000), Integer.valueOf(456000), Integer.valueOf(608000), Integer.valueOf(760000), Integer.valueOf(816000), Integer.valueOf(912000), Integer.valueOf(1000000)};
        this.freqText_nvidia_tegra2 = new String[]{"216000", "312000", "456000", "608000", "760000", "816000", "912000", "1000000"};
        this.defaultMax_nvidia_tegra2 = 1000000;
        this.defaultMin_nvidia_tegra2 = 216000;
        this.limitMax_nvidia_tegra2 = 1000000000;
        this.limitMin_nvidia_tegra2 = 0;
        this.freq = new Integer[]{Integer.valueOf(0)};
        this.freqText = new String[]{"0"};
        this.defaultMax = 0;
        this.defaultMin = 0;
        this.limitMax = 0;
        this.limitMin = 0;
        if (config.contains("nvidia_tegra2")) {
            this.freq = this.freq_nvidia_tegra2;
            this.freqText = this.freqText_nvidia_tegra2;
            this.defaultMax = 1000000;
            this.defaultMin = 216000;
            this.limitMax = 1000000000;
            this.limitMin = 0;
        }
    }

    public Integer[] getFreq() {
        return this.freq;
    }

    public String[] getFreqText() {
        return this.freqText;
    }

    public int getDefaultMax() {
        return this.defaultMax;
    }

    public int getDefaultMin() {
        return this.defaultMin;
    }

    int getLimitMax() {
        return this.limitMax;
    }

    int getLimitMin() {
        return this.limitMin;
    }
}
