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
import android.widget.Toast;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static com.example.marcos.bluetoothtest.BluetoothHelper.REQUEST_ENABLE_BTH;

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

    @Override
    public void setPairedDevices(Set<BluetoothDevice> pairedDevices) {

    }

    @Override
    public void setFoundDevices(Map<String, BluetoothDevice> devicesFound) {
        Log.e(TAG, "found devices");

        Set<String> keys = devicesFound.keySet();
        for(Iterator<String> i = keys.iterator(); i.hasNext(); ){
            String key = i.next();
            BluetoothDevice device = devicesFound.get(key);

            StringBuilder builder = new StringBuilder();
            ParcelUuid [] uuids = device.getUuids();
            if( uuids != null ){
                for(int z = 0; z < uuids.length; z++ ){
                    UUID uuid = uuids[z].getUuid();
                    if( uuid != null){
                        builder.append(uuid.toString() + "\n");
                    }
                    uuid = null;
                }
            }

            Log.e(TAG, "Device Address: " + device.getAddress()
                    + ", Device name " + device.getName() +
                    " Device UUID " + builder.toString());

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