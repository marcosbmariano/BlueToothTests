package com.example.marcos.bluetoothtest;

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
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

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
    // BroadcastReceiver for ACTION_FOUND.
    private static BroadcastReceiver mReceiver;


    private BluetoothHelper(){
        if( mAppContext == null ){
            throw new IllegalStateException("Application context cannot be null," +
                    " set application context before call first call to getInstance");
        }
        mBTHAdapter = BluetoothAdapter.getDefaultAdapter();

    }
    public static void setApplicationContext(Context applicationContext){
        mAppContext = applicationContext;
    }

    public static BluetoothHelper getInstance(){
        return Holder.sIntance;
    }

    private static class Holder{
        private final static BluetoothHelper sIntance = new BluetoothHelper();
    }

    public static void registerObserver(BluetoothObserver listener){
        mObserver = listener;
//        synchronized (observerLock) {
//            if(mObservers.isEmpty()){
               createReceiver();
               IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
               mAppContext.registerReceiver(mReceiver, filter);
//            }
//            mObservers.put(listener.getClass().toString(), listener);
//
//        }
    }

    public static void unregisterObserver(BluetoothObserver listener){
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
    }

    private static void createReceiver(){
        mReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if(BluetoothDevice.ACTION_FOUND.equals(action)){
                    Log.d(TAG, "OnReceive");
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    mNearbyDevices.put(device.getName(), device);
//                    Set<String> keys = mObservers.keySet();
//
//                    for( Iterator<String> i = keys.iterator(); i.hasNext(); ){
//                        String key = i.next();
//                        //if( mObservers.containsKey(key)){
//                            mObservers.get(key).setFoundDevices(mNearbyDevices);
//                      //  }
//                    }
                        mObserver.setFoundDevices(mNearbyDevices);
                }
            }
        };
    }

    public void connetToDevice( String macAdress){

    }


    public void isBluetoothEnabled(){
        IsBluetoothEnabledAsyncTask task = new IsBluetoothEnabledAsyncTask();
        task.execute(mBTHAdapter);
    }

    private class IsBluetoothEnabledAsyncTask extends AsyncTask<BluetoothAdapter, Boolean, Boolean>{

        @Override
        protected Boolean doInBackground(BluetoothAdapter... params) {
           return params[0].isEnabled();
        }

        @Override
        protected void onPostExecute(Boolean isEnabled) {
            //Set<String> keys = mObservers.keySet();
            String key;
//            for( Iterator<String> i = keys.iterator(); i.hasNext();  ){
//                key = i.next();
//                if( mObservers.containsKey(key)){
//                    mObservers.get(key).isBluetoothEnabled(isEnabled);
//                }
//            }
            mObserver.isBluetoothEnabled(isEnabled);
        }
    }

    public boolean hasBluetooth(){
        return null != mBTHAdapter;
    }

    public void findBondedDevices(){
        Log.d(TAG, "findBondedDevices");
        mPairedDevices = mBTHAdapter.getBondedDevices();
        StringBuilder devices;
        if(mPairedDevices.size() > 0){
            devices = new StringBuilder();
            for(BluetoothDevice device: mPairedDevices){
                mPairedDevices.add(device);
            }

        }else{
            //Toast.makeText(this, "No paired devices", Toast.LENGTH_LONG).show();
        }
    }


    public void startDevicesDiscovery(){
        Log.d(TAG, "startDevicesDiscovery");
        mBTHAdapter.startDiscovery();
    }

    public void cancelDiscovery(){
        mBTHAdapter.cancelDiscovery();
    }

    public final class NearbyBluetoothReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(BluetoothDevice.ACTION_FOUND.equals(action)){
                Log.d(TAG, "OnReceive");
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                mNearbyDevices.put(device.getName(), device);
                Log.d(TAG, "Device Address: " + device.getAddress()
                        + ", Device name " + device.getName() +
                        " Device UUID " + device.getUuids().toString());

                mObserver.setFoundDevices(mNearbyDevices);
            }
        }
    }


    private void sendFoundDevicesToObservers(BluetoothDevice device){
       Log.d(TAG, "sendFoundDevicesToObservers");
//        // synchronization will not work with garbage collection
//        //Set<String> keys = mObservers.keySet();
//        String key;
//        BluetoothObserver observer;
//        for(Iterator<String> i = keys.iterator(); i.hasNext(); ){
//            key = i.next();
//            observer = mObservers.get(key);
//            if( observer != null ){
//                observer.setFoundDevices(mNearbyDevices);
//            }
//        }


    }

    public void connectToRemoteBTH(String info){
        BluetoothDevice device = mNearbyDevices.get(info);

        ClientThread thread = new ClientThread(device);
        thread.start();
    }

    private class ClientThread extends Thread{
        private static final String TAG = "ClientThread";
        private final BluetoothSocket mBTHSocket;

        private ClientThread( BluetoothDevice device){
            BluetoothSocket tmpSocket = null;

            try {
                tmpSocket = device.createRfcommSocketToServiceRecord(UUIDManager.getUUID());
            } catch (IOException e) {
                Log.e(TAG, "Socket's create() method failed", e);
            }

            mBTHSocket = tmpSocket;
        }

        @Override
        public void run() {
            // Cancel discovery because it otherwise slows down the connection.
            mBTHAdapter.cancelDiscovery();

            try {
                mBTHSocket.connect();
                manageMyConnection(mBTHSocket);
            } catch (IOException e) {

                try {
                    mBTHSocket.close();
                } catch (IOException e1) {
                    Log.e(TAG, "Could not close the client socket",e1);
                }
                return;
            }

        }

        private void manageMyConnection(BluetoothSocket socket){
            //handle communication with device
        }

        private void cancel(){
            if( mBTHSocket != null){
                try {
                    mBTHSocket.close();
                } catch (IOException e) {
                    Log.e(TAG, "Could not close the client socket",e);
                }
            }
        }
    }




    interface BluetoothObserver {
        void isBluetoothEnabled(boolean isEnabled);
        void setPairedDevices(Set<BluetoothDevice> pairedDevices);
        void setFoundDevices(Map<String, BluetoothDevice> devicesFound);
    }

}
