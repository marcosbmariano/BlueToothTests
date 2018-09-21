package com.example.marcos.bluetoothtest.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;

/**
 * Created by marcos on 1/25/17.
 */

public class ServerTask extends AsyncTask<BluetoothAdapter, Integer, BluetoothSocket> {
    private static final String TAG = "ServerTask";


    @Override
    protected BluetoothSocket doInBackground(BluetoothAdapter... params) {
        BluetoothServerSocket serverSocket = null;
        BluetoothAdapter adapter = params[0];
        try {
            serverSocket = adapter.listenUsingRfcommWithServiceRecord(adapter.getName(),
                    UUIDManager.getUUID());
        } catch (IOException e) {
            Log.e(TAG, "Socket's listen() method failed", e);
        }

        BluetoothSocket socket = null;

        while (true) {
            if (serverSocket == null) {
                throw new IllegalStateException("ServerSocket is Null");
            }

            try {
                socket = serverSocket.accept();
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (socket != null) {
                try {
                    serverSocket.close();
                } catch (IOException e) {
                    Log.e(TAG, "Socket's close() method failed", e);
                }
                return socket;
            }
        }
    }

    @Override
    protected void onPostExecute(BluetoothSocket socket) {
        BluetoothHelper.getInstance().setServerSocket(socket);
    }
}
