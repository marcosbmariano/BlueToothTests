package com.example.marcos.bluetoothtest.bluetooth;

import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by marcos on 1/26/17.
 */

public class BluetoothConnectionManager {
    private final static String TAG = "BTHConnManager";
    private Handler mHandler;

    private interface MessageConstants{
        public static final int MESSAGE_READ = 0;
        public static final int MESSAGE_WRITE = 1;
        public static final int MESSAGE_TOAST = 2;

    }

    private class ConnectedThread extends Thread{
        private final BluetoothSocket mSocket;
        private final InputStream mInStream;
        private final OutputStream mOutStream;
        private byte [] mBuffer;

        public ConnectedThread (BluetoothSocket socket){
            mSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            try {
                tmpIn = mSocket.getInputStream();
            } catch (IOException e) {
                Log.e(TAG, "Error occurred when creating input stream", e);
            }

            try {
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }

            mInStream = tmpIn;
            mOutStream = tmpOut;
        }


        @Override
        public void run() {
            mBuffer = new byte[1024];
            int bytesFromRead;

            // Keep listening to the InputStream until an exception occurs.
            while (true){
                try {
                    bytesFromRead = mInStream.read(mBuffer);

                    Message readMsg = mHandler.obtainMessage(
                            MessageConstants.MESSAGE_READ,
                            bytesFromRead,
                            -1,
                            mBuffer);
                    readMsg.sendToTarget();
                } catch (IOException e) {
                    Log.d(TAG, "Input stream was disconnected", e);
                    break;
                }
            }
        }

        public void write(byte[] bytes){
            try{
                mOutStream.write(bytes);
                // Share the sent message with the UI activity.
                Message writtenMsg = mHandler.obtainMessage(
                        MessageConstants.MESSAGE_WRITE, -1,-1, mBuffer);
                writtenMsg.sendToTarget();


            } catch (IOException e) {
                Log.e(TAG, "Error occurred when sending data", e);
                Message writeErrorMsg = mHandler.obtainMessage(MessageConstants.MESSAGE_TOAST);
                Bundle bundle = new Bundle();
                bundle.putString("toast", "Couldn't send data to the other device!");
                writeErrorMsg.setData(bundle);
                mHandler.sendMessage(writeErrorMsg);
            }
        }

        // Call this method from the main activity to shut down the connection.
        public void endConnection() {
            try {
                mSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "Could not close the connect socket", e);
            }
        }

    }


}









































//