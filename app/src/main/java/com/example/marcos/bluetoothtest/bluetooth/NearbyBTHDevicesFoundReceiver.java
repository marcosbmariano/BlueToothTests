package com.example.marcos.bluetoothtest.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;


public class NearbyBTHDevicesFoundReceiver extends BroadcastReceiver {
    private final static String TAG = "NEARBYTASKDEV";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (BluetoothDevice.ACTION_FOUND.equals(action)) {
            Log.d(TAG, "OnReceive");
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            BluetoothHelper.getInstance().addFoundDevices(device.getAddress(), device);
        }

    }
}
