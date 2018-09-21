package com.example.marcos.bluetoothtest.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.os.AsyncTask;

/**
 * Created by marcos on 1/25/17.
 */

public class CheckBluetoothEnabledTask extends AsyncTask<BluetoothAdapter, Boolean, Boolean> {

    @Override
    protected Boolean doInBackground(BluetoothAdapter... params) {
        return params[0].isEnabled();
    }

    @Override
    protected void onPostExecute(Boolean isEnabled) {
        BluetoothHelper.getInstance().setBluetoothEnableFromTask(isEnabled);
    }
}

