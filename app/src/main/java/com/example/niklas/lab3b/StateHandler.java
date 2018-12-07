package com.example.niklas.lab3b;

public class StateHandler {
    private static BTConnectionState btConnectionState;
    private StateHandler stateHandler;


    public static void initConnectionState(MainActivity mainActivity) {
        btConnectionState = new ScanForDeviceState(mainActivity);
    }

    public static boolean initBLE() {
        if (btConnectionState != null) {
            return btConnectionState.initBLE();
        }
        return false;
    }

    public static void scanLeDevice(boolean enable) {
        btConnectionState.scanLeDevice(enable);
    }
}
