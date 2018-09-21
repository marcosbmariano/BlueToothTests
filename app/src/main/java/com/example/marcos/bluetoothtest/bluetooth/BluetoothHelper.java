package com.example.marcos.bluetoothtest.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by marcos on 1/18/17.
 */

public class BluetoothHelper {
    private final BluetoothAdapter mBTHAdapter;
    private static Object observerLock = new Object(); //this is the lock for mObserver;
    //private static Map<String, BluetoothObserver> mObservers = new WeakHashMap<>();
    static BluetoothObserver mObserver;
    private Set<BluetoothDevice> mPairedDevices = new HashSet<>();
    private static Map<String, BluetoothDevice> mNearbyDevices = new HashMap<>();
    public static final int REQUEST_ENABLE_BTH = 781;
    private static final String TAG = "BluetoothHelper";
    private static Context mAppContext;
    private static NearbyBTHDevicesFoundReceiver mNearbyBTHReceiver;

    private BluetoothHelper() {
        if (mAppContext == null) {
            throw new IllegalStateException("Application context cannot be null," +
                    " set application context before call first call to getInstance");
        }
        mBTHAdapter = BluetoothAdapter.getDefaultAdapter();

    }

    public static void setApplicationContext(Context applicationContext) {
        mAppContext = applicationContext;
    }

    public static BluetoothHelper getInstance() {
        return Holder.sIntance;
    }

    private static class Holder {
        private final static BluetoothHelper sIntance = new BluetoothHelper();
    }

    public static void registerObserver(BluetoothObserver listener) {
        mObserver = listener;

        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        if (mNearbyBTHReceiver == null) {
            mNearbyBTHReceiver = new NearbyBTHDevicesFoundReceiver();
        }
        mAppContext.registerReceiver(mNearbyBTHReceiver, filter);

    }

    public static void unregisterObserver(BluetoothObserver listener) {
//        synchronized (observerLock){
//            if( mObservers.containsKey(listener.getClass().toString())){
//                mObservers.remove(listener);  //SHould I use reentrant lock?
//            }
//
//            if( mObservers.isEmpty()){
//                mAppContext.unregisterReceiver(mReceiver);
//            }
//
//        }
        mObserver = null;
    }


    public void connectToRemoteBTH(BluetoothDevice device) {
        ClientTask task = new ClientTask();
        task.execute(device);
    }

    void setClientSocket(BluetoothSocket socket) {
        Log.e(TAG, "Here is the socket " + socket.toString());
    }

    public void startBTHServer(){
        ServerTask task = new ServerTask();
        task.execute(mBTHAdapter);
    }

    void setServerSocket(BluetoothSocket socket){
        Log.e(TAG, "Server opened");
    }

    public void isBluetoothEnabled() {
        CheckBluetoothEnabledTask task = new CheckBluetoothEnabledTask();
        task.execute(mBTHAdapter);
    }

    void setBluetoothEnableFromTask(boolean isEnabled) {
        mObserver.isBluetoothEnabled(isEnabled);
    }

    public boolean hasBluetooth() {
        return null != mBTHAdapter;
    }

    public void findBondedDevices() {
        Log.d(TAG, "findBondedDevices");
        mPairedDevices = mBTHAdapter.getBondedDevices();
        StringBuilder devices;
        if (mPairedDevices.size() > 0) {
            devices = new StringBuilder();
            for (BluetoothDevice device : mPairedDevices) {
                mPairedDevices.add(device);
            }

        } else {
            //Toast.makeText(this, "No paired devices", Toast.LENGTH_LONG).show();
        }
    }


    public void startDevicesDiscovery() {
        Log.d(TAG, "startDevicesDiscovery");
        mBTHAdapter.startDiscovery();
    }

    void addFoundDevices(String address, BluetoothDevice device) {
        mNearbyDevices.put(address, device);
        mObserver.setFoundDevices(mNearbyDevices);
    }

    public void cancelDiscovery() {
        mBTHAdapter.cancelDiscovery();
    }


    public interface BluetoothObserver {
        void isBluetoothEnabled(boolean isEnabled);

        void setPairedDevices(Set<BluetoothDevice> pairedDevices);

        void setFoundDevices(Map<String, BluetoothDevice> devicesFound);
    }

}
