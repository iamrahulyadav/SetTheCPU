package com.ansoft.speedup;

public class Jni {


    public static native String cbench();

    public static native String neonbench();

    static {
        System.loadLibrary("SpeedUp-native");
    }
}
