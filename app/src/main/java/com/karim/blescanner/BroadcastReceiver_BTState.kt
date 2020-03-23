package com.karim.blescanner

import android.bluetooth.BluetoothAdapter
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent


class BroadcastReceiver_BTState(internal var activityContext: Context) : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action

        if (action == BluetoothAdapter.ACTION_STATE_CHANGED) {
            val state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR)

            when (state) {
                BluetoothAdapter.STATE_OFF -> Utils.toast(activityContext, "Bluetooth is off")
                BluetoothAdapter.STATE_TURNING_OFF -> Utils.toast(
                    activityContext,
                    "Bluetooth is turning off..."
                )
                BluetoothAdapter.STATE_ON -> Utils.toast(activityContext, "Bluetooth is on")
                BluetoothAdapter.STATE_TURNING_ON -> Utils.toast(
                    activityContext,
                    "Bluetooth is turning on..."
                )
            }
        }
    }
}
