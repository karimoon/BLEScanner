package com.karim.blescanner

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothGattCharacteristic
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.view.Gravity
import android.widget.Toast


object Utils {

    fun checkBluetooth(bluetoothAdapter: BluetoothAdapter?): Boolean {

        // Ensures Bluetooth is available on the device and it is enabled. If not,
        // displays a dialog requesting user permission to enable Bluetooth.
        return if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled) {
            false
        } else {
            true
        }
    }

    fun requestUserBluetooth(activity: Activity) {
        val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
        activity.startActivityForResult(enableBtIntent, MainActivity.REQUEST_ENABLE_BT)
    }

    fun makeGattUpdateIntentFilter(): IntentFilter {

        val intentFilter = IntentFilter()

        intentFilter.addAction(Service_BTLE_GATT.ACTION_GATT_CONNECTED)
        intentFilter.addAction(Service_BTLE_GATT.ACTION_GATT_DISCONNECTED)
        intentFilter.addAction(Service_BTLE_GATT.ACTION_GATT_SERVICES_DISCOVERED)
        intentFilter.addAction(Service_BTLE_GATT.ACTION_DATA_AVAILABLE)

        return intentFilter
    }

    fun hexToString(data: ByteArray): String {
        val sb = StringBuilder(data.size)

        for (byteChar in data) {
            sb.append(String.format("%02X ", byteChar))
        }

        return sb.toString()
    }

    fun hasWriteProperty(property: Int): Int {
        return property and BluetoothGattCharacteristic.PROPERTY_WRITE
    }

    fun hasReadProperty(property: Int): Int {
        return property and BluetoothGattCharacteristic.PROPERTY_READ
    }

    fun hasNotifyProperty(property: Int): Int {
        return property and BluetoothGattCharacteristic.PROPERTY_NOTIFY
    }

    fun toast(context: Context, string: String) {

        val toast = Toast.makeText(context, string, Toast.LENGTH_SHORT)
        toast.setGravity(Gravity.CENTER or Gravity.BOTTOM, 0, 0)
        toast.show()
    }
}
