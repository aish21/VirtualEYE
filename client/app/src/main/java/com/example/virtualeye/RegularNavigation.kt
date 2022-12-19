package com.example.virtualeye

import android.R.id
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.davemorrissey.labs.subscaleview.ImageSource
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView


var mBluetoothLeScanner: BluetoothLeScanner? = null
var mScanCallback: ScanCallback? = null

class RegularNavigation : AppCompatActivity() {
    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.regular_navigation)

        val button = findViewById<ImageButton>(R.id.startScanButton)
        val imageView = findViewById<View>(R.id.imageView) as SubsamplingScaleImageView
        imageView.setImage(ImageSource.resource(R.drawable.scse_1_final));

        button.setOnClickListener {
            val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()

            if (bluetoothAdapter == null) {
                Toast.makeText(this, "Bluetooth", Toast.LENGTH_SHORT).show()
                finish()
            }

            mBluetoothLeScanner = bluetoothAdapter?.bluetoothLeScanner

            mScanCallback = initCallbacks()
            //println(mScanCallback.toString())
            //println("mScanCallback")

            mBluetoothLeScanner?.startScan(mScanCallback)

        }
    }

    private fun initCallbacks(): ScanCallback {
        val name1 = "FCL Beacon1"
        val name2 = "FWM8BLZ02"
        //val myRes = findViewById<TextView>(R.id.myEqn)
        //val notMyRes = findViewById<TextView>(R.id.researchedEqn)

        return object : ScanCallback() {
            @SuppressLint("MissingPermission", "NewApi")
            override fun onScanResult(
                callbackType: Int,
                result: ScanResult
            ) {
                super.onScanResult(callbackType, result)

                if (result.device != null) {
                    if(result.device.name != null) {
                        //addDevice(result.getDevice(), result.getRssi());
                        if(result.device.name.equals(name1) or result.device.name.equals(name2)) {
                            Log.i("BLE NAME: ", result.device.name)
                            //println(result.device.name)
                            Log.i("BLE MAC: ", result.device.toString())
                            //println(result.device)
                            Log.i("BLE RSSI: ", result.rssi.toString())
                            //println(result.rssi)

                            //val calcMyRes = (-0.113 * result.rssi.toFloat())  - 61.1
                            //val calcNotMyRes = 10.0.pow((((-77.0 - result.rssi.toFloat()) / (10 * 2))))

                            //myRes.text = result.rssi.toString()
                            //notMyRes.text = calcNotMyRes.toString()
                        }
                    }
                }
                return
            }
        }
    }
}