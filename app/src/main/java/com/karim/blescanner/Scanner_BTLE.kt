package com.karim.blescanner

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.Context
import android.os.Handler
import android.util.Log


class Scanner_BTLE(
    private val ma: MainActivity,
    private val scanPeriod: Long,
    private val signalStrength: Int
) {

    private val mBluetoothAdapter: BluetoothAdapter
    var isScanning: Boolean = false
        private set
    private lateinit var mHandler: Handler

    // Device scan callback.
    private val mLeScanCallback = BluetoothAdapter.LeScanCallback { device, rssi, scanRecord ->
        Log.d("device", device.address)

        if (rssi > signalStrength) {
            mHandler.post { ma.addDevice(device, rssi) }
        }
    }

    init {

        mHandler = Handler()

        val bluetoothManager = ma.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        mBluetoothAdapter = bluetoothManager.adapter
    }

    fun start() {
        if (!Utils.checkBluetooth(mBluetoothAdapter)) {
            Utils.requestUserBluetooth(ma)
            ma.stopScan()
        } else {
            scanLeDevice(true)
        }
    }

    fun stop() {
        scanLeDevice(false)
    }

    // If you want to scan for only specific types of peripherals,
    // you can instead call startLeScan(UUID[], BluetoothAdapter.LeScanCallback),
    // providing an array of UUID objects that specify the GATT services your app supports.
    private fun scanLeDevice(enable: Boolean) {
        if (enable && !isScanning) {
            Utils.toast(ma.applicationContext, "Starting BLE scan...")

            // Stops scanning after a pre-defined scan period.
            mHandler.postDelayed({
                Utils.toast(ma.applicationContext, "Stopping BLE scan...")

                isScanning = false
                mBluetoothAdapter.stopLeScan(mLeScanCallback)

                ma.stopScan()
            }, scanPeriod)

            isScanning = true
            mBluetoothAdapter.startLeScan(mLeScanCallback)
            //            mBluetoothAdapter.startLeScan(uuids, mLeScanCallback);
        } else {
            isScanning = false
            mBluetoothAdapter.stopLeScan(mLeScanCallback)
        }
    }
}