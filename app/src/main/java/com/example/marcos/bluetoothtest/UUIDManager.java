package com.example.marcos.bluetoothtest;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.ParcelUuid;
import android.util.Log;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.UUID;

import static android.content.ContentValues.TAG;

/**
 * Created by marcos on 1/17/17.
 */

public class UUIDManager {

    //private static final String UUIDSTR = "ae7a9a9f-7103-473b-b762-b6b3ea8fe4ad";
    private static final String UUIDSTR = "0000-1000-8000-00805f9b34fb";
    private UUIDManager(){}

    public static UUID getUUID(){
        return UUID.fromString(UUIDSTR);
    }

    public static ArrayList<String> getUUIDFromDevice(BluetoothAdapter bthAdapter){

        ArrayList<String> result = new ArrayList<>();
        try {
            Method getUUIDsMethod = BluetoothAdapter.class.getDeclaredMethod("getUuids", null);

            ParcelUuid[] uuids = (ParcelUuid[]) getUUIDsMethod.invoke(bthAdapter, null);

            for( ParcelUuid uuid: uuids){
                Log.d("GETUUID", "UUID: " + uuid.getUuid().toString());
            }

        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return result;
    }


}
