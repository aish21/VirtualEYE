package com.example.virtualeye

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import android.bluetooth.BluetoothAdapter
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.widget.Button
import android.widget.Toast

var mBluetoothLeScanner: BluetoothLeScanner? = null
var mScanCallback: ScanCallback? = null

class MainActivity : AppCompatActivity() {
    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val button = findViewById<Button>(R.id.startScanButton)
        button.setOnClickListener {
            val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()

            if (bluetoothAdapter == null) {
                Toast.makeText(this, "Bluetooth", Toast.LENGTH_SHORT).show()
                finish()
            }

            mBluetoothLeScanner = bluetoothAdapter?.bluetoothLeScanner

            mScanCallback = initCallbacks()
            println(mScanCallback.toString())
            println("mScanCallback")

            mBluetoothLeScanner?.startScan(mScanCallback)

        }
    }

    private fun initCallbacks(): ScanCallback {
        return object : ScanCallback() {
            @SuppressLint("MissingPermission")
            override fun onScanResult(
                callbackType: Int,
                result: ScanResult
            ) {
                super.onScanResult(callbackType, result)

                if (result.device != null) {
                    //addDevice(result.getDevice(), result.getRssi());
//                    if(result.device.name == "FWM8BLZ02"){
                        println(result.device.name)
                        println(result.device)
                        println(result.rssi)
                        println(" ")
                        println(" ")

//                    }
                }
                return
            }
        }
    }
}