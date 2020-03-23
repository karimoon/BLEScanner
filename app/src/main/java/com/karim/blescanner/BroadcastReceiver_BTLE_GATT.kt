package com.karim.blescanner

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent


class BroadcastReceiver_BTLE_GATT(private val activity: Activity_BTLE_Services) :
    BroadcastReceiver() {

    private var mConnected = false

    // Handles various events fired by the Service.
    // ACTION_GATT_CONNECTED: connected to a GATT server.
    // ACTION_GATT_DISCONNECTED: disconnected from a GATT server.
    // ACTION_GATT_SERVICES_DISCOVERED: discovered GATT services.
    // ACTION_DATA_AVAILABLE: received data from the device. This can be a
    // result of read or notification operations.

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action

        if (Service_BTLE_GATT.ACTION_GATT_CONNECTED == action) {
            mConnected = true
        } else if (Service_BTLE_GATT.ACTION_GATT_DISCONNECTED == action) {
            mConnected = false
            Utils.toast(context, "Disconnected From Device")
            // ((Activity_BTLE_Services)context)
            activity.finish()
        } else if (Service_BTLE_GATT.ACTION_GATT_SERVICES_DISCOVERED == action) {
            activity.updateServices()
        } else if (Service_BTLE_GATT.ACTION_DATA_AVAILABLE == action) {

            //            String uuid = intent.getStringExtra(Service_BTLE_GATT.EXTRA_UUID);
            //            String data = intent.getStringExtra(Service_BTLE_GATT.EXTRA_DATA);

            activity.updateCharacteristic()
        }

        return
    }
}
