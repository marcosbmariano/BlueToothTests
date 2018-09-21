package com.example.marcos.bluetoothtest;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.ParcelUuid;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;

import com.example.marcos.bluetoothtest.bluetooth.BluetoothHelper;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static com.example.marcos.bluetoothtest.bluetooth.BluetoothHelper.REQUEST_ENABLE_BTH;

public class MainActivity extends AppCompatActivity implements BluetoothHelper.BluetoothObserver,
        AdapterView.OnClickListener {

    private Button mBtnConnectAsClient;
    private Button mBtnFindPairedDev;
    private Button mBtnFindDevices;
    private Button mBtnStartServer;

    private TextView mTextView;
    private BluetoothHelper mBTHHelper;
    private final static String TAG = "MAinActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Example of a call to a native method
        TextView tv = (TextView) findViewById(R.id.sample_text);
        tv.setText(stringFromJNI());

        long heapSize = Runtime.getRuntime().maxMemory();
        Log.e(TAG, "Max Heap size: " + heapSize/(1024 * 8));


        setupViews();
        BluetoothHelper.setApplicationContext(this.getApplicationContext());
        mBTHHelper = BluetoothHelper.getInstance();
        Log.d(TAG, "On Create");

    }

    @Override
    protected void onResume() {
        BluetoothHelper.registerObserver(this);
        if( mBTHHelper.hasBluetooth()){
            mBTHHelper.isBluetoothEnabled();
        }

        super.onResume();
    }

    private void setupViews(){
        Log.d(TAG, "setupViews");
        mBtnConnectAsClient = (Button)findViewById(R.id.btn_client);
        mBtnConnectAsClient.setOnClickListener(this);

        mBtnFindPairedDev = (Button)findViewById(R.id.btn_paired_devices);
        mBtnFindPairedDev.setOnClickListener(this);

        mBtnFindDevices = (Button)findViewById(R.id.btn_find_devices);
        mBtnFindDevices.setOnClickListener(this);

        mTextView = (TextView)findViewById(R.id.sample_text);

        mBtnStartServer = (Button)findViewById(R.id.btn_start_server);
        mBtnStartServer.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        Log.d(TAG, "On Click");
        switch (v.getId()){
            case R.id.btn_client:
                Log.e(TAG, "Connect Devices");
                //mBTHHelper.connetToDevice(null);
                connectToBTHDevice();
                break;
            case R.id.btn_find_devices:
                Log.e(TAG, "Find Devices");
                mBTHHelper.startDevicesDiscovery();
                break;

            case R.id.btn_paired_devices:
                Log.e(TAG, "findBondedDevices");
               // mBTHHelper.findBondedDevices();
                break;

            case R.id.btn_start_server:
                Log.e(TAG, "Start As Server");
                BluetoothHelper.getInstance().startBTHServer();
                break;
            default:
                //DO nothing
        }
    }


    @Override
    public void isBluetoothEnabled(boolean isEnabled) {
        if( !isEnabled ){
            Intent enableBTH = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBTH, REQUEST_ENABLE_BTH);
        }else{
            Log.d(TAG, "Bluetooth is enabled!");
        }
    }

    private void connectToBTHDevice(){

        BluetoothDevice device = mDevicesFound.get("98:0D:2E:CD:D3:53");
        if(device != null){
            Log.e(TAG, "Device found: " + device.getName());
            mBTHHelper.connectToRemoteBTH(device);
        }else{
            Log.e(TAG, "EXPECTED DEVICE NOT FOUND!");
        }

    }

    @Override
    public void setPairedDevices(Set<BluetoothDevice> pairedDevices) {

    }
    private  Map<String, BluetoothDevice> mDevicesFound;
    @Override
    public void setFoundDevices(Map<String, BluetoothDevice> devicesFound) {
        Log.e(TAG, "found devices");
        mDevicesFound = devicesFound;

        Set<String> keys = devicesFound.keySet();
        for( Iterator<String>i = keys.iterator(); i.hasNext(); ){
            String key = i.next();
            BluetoothDevice  device = devicesFound.get(key);
            Log.e(TAG, "Device name: " + device.getName() + " device address " + key );
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if( requestCode == REQUEST_ENABLE_BTH && resultCode == RESULT_OK ){
            //Toast.makeText(this, "Bluetooth is enabled!", Toast.LENGTH_LONG).show();
        }

    }

    @Override
    protected void onDestroy() {
        BluetoothHelper.unregisterObserver(this);
        super.onDestroy();
    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }



}





















































//