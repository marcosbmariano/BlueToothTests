package com.example.marcos.bluetoothtest.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;

/**
 * Created by marcos on 1/25/17.
 */

public class ClientTask extends AsyncTask<BluetoothDevice,Integer, BluetoothSocket> {
    private static final String TAG = "ClientTask";

    @Override
    protected BluetoothSocket doInBackground(BluetoothDevice... params) {
        BluetoothHelper.getInstance().cancelDiscovery();
        BluetoothDevice device = params[0];
        BluetoothSocket tmpSocket = null;
        try {
            tmpSocket = device.createRfcommSocketToServiceRecord(UUIDManager.getUUID());
            tmpSocket.connect();
        } catch (IOException e) {
            Log.e(TAG, "Socket's create() method failed", e);
            try {
                tmpSocket.close();
            } catch (IOException e1) {
                Log.e(TAG, "Could not close the client socket",e1);
            }
        }

        return tmpSocket;
    }

    @Override
    protected void onPostExecute(BluetoothSocket socket) {
        BluetoothHelper.getInstance().setClientSocket(socket);
    }


}

