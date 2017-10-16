package com.zackeryrobinson.bluetooth;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.Intent;
import android.content.pm.LauncherApps;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.util.List;
import java.util.Set;


public class MainActivity extends AppCompatActivity {

    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 12;
    private boolean mScanning;
    private Handler mHandler = new Handler();

    // Stops scanning after 10 seconds.
    private static final long SCAN_PERIOD = 10000;
    //The system passes this constant back to you
    // in your onActivityResult() implementation as the requestCode parameter.
    private static final int REQUEST_ENABLE_BT = 1;
    private static final String TAG = "MainActivityTag";
    private BluetoothAdapter mBluetoothAdapter;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }

        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }

        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Log.d(TAG, "onCreate: BLE not supported");
            Toast.makeText(this, "BLE not supported", Toast.LENGTH_LONG).show();
            finish();
        } else {
            Log.d(TAG, "onCreate: BLE supported");
            Toast.makeText(this, "BLE supported", Toast.LENGTH_LONG).show();
        }

        checkPermissions();

    }


    private void checkPermissions() {
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

            } else {

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION
                                , Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);

            }
        }
    }

    private void getMyBondedDevices(Set<BluetoothDevice> pairedDevices) {
        if (pairedDevices.size() > 0) {
            // There are paired devices. Get the name and address of each paired device.
            for (BluetoothDevice device : pairedDevices) {
                String deviceName = device.getName();
                String deviceHardwareAddress = device.getAddress(); // MAC address
                Log.d(TAG, "getMyBondedDevices: DeviceName: " + deviceName + ", Device Hardware Address: " + deviceHardwareAddress);
            }
        }
    }

    public void getMyBondedDevices(View view) {
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        getMyBondedDevices(pairedDevices);
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void scanLeDevice() {

        final ScanCallback sc = new ScanCallback() {
            @Override
            public void onScanResult(int callbackType, ScanResult result) {
                super.onScanResult(callbackType, result);
                Log.d(TAG, "onScanResult: " + result);
            }

            @Override
            public void onBatchScanResults(List<ScanResult> results) {
                super.onBatchScanResults(results);
                for (ScanResult result :
                        results) {
                    Log.d(TAG, "onBatchScanResults: " + result);
                }
            }

            @Override
            public void onScanFailed(int errorCode) {
                super.onScanFailed(errorCode);
                Log.d(TAG, "onScanFailed: " + errorCode);
            }
        };

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mBluetoothAdapter.getBluetoothLeScanner().startScan(sc);

            }
        }, SCAN_PERIOD);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void startScan(View view) {
        scanLeDevice();
    }
}

