package com.karim.blescanner

import android.bluetooth.BluetoothDevice

class BTLE_Device(private val bluetoothDevice: BluetoothDevice) {
    var rssi: Int = 0

    val address: String
        get() =  if(bluetoothDevice.address != null) bluetoothDevice.address else "NoAddress"

    val name: String
        get() = if(bluetoothDevice.name != null) bluetoothDevice.name else "NoName"
}
