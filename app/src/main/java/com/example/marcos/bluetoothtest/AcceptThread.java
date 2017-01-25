package com.example.marcos.bluetoothtest;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;

/**
 * Created by marcos on 1/17/17.
 */

public class AcceptThread extends Thread {
    private final static String TAG = "ACCEPT_THREAD";
    private final BluetoothServerSocket mServerSocket;

    public AcceptThread(final BluetoothAdapter adapter){
        BluetoothServerSocket tmp = null;

        try {
            tmp = adapter.listenUsingRfcommWithServiceRecord(adapter.getName(), UUIDManager.getUUID());
        } catch (IOException e) {
            Log.e(TAG, "Socket's listen() method failed", e);
        }

        mServerSocket = tmp;
    }


    public void run(){

        BluetoothSocket socket = null;

        while(true){
            if( mServerSocket == null){
                throw new IllegalStateException("ServerSocket is Null");
            }

            try {
                socket = mServerSocket.accept();
            } catch (IOException e) {
                e.printStackTrace();
            }

            if( socket != null){
                manageMyConnectedSocket(socket);
                try {
                    mServerSocket.close();
                } catch (IOException e) {
                    Log.e(TAG, "Socket's close() method failed", e);
                }
                break;
            }
        }

    }

    private void manageMyConnectedSocket(BluetoothSocket socket){
        Log.d(TAG, socket.getRemoteDevice().getName());
    }
}
