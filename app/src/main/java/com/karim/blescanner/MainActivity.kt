package com.karim.blescanner

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.Button
import android.widget.ListView
import android.widget.ScrollView

import androidx.appcompat.app.AppCompatActivity

import java.util.ArrayList
import java.util.HashMap

class MainActivity : AppCompatActivity(), View.OnClickListener, AdapterView.OnItemClickListener {

    private var mBTDevicesHashMap: HashMap<String, BTLE_Device>? = null
    private var mBTDevicesArrayList: ArrayList<BTLE_Device>? = null
    private var adapter: ListAdapter_BTLE_Devices? = null
    private var listView: ListView? = null

    private var btn_Scan: Button? = null

    private var mBTStateUpdateReceiver: BroadcastReceiver_BTState? = null
    private var mBTLeScanner: Scanner_BTLE? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Use this check to determine whether BLE is supported on the device. Then
        // you can selectively disable BLE-related features.
        if (!packageManager.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Utils.toast(applicationContext, "BLE not supported")
            finish()
        }

        mBTStateUpdateReceiver = BroadcastReceiver_BTState(applicationContext)
        mBTLeScanner = Scanner_BTLE(this, 5000, -99)

        mBTDevicesHashMap = HashMap()
        mBTDevicesArrayList = ArrayList()

        adapter =
            ListAdapter_BTLE_Devices(this, R.layout.btle_device_list_item, mBTDevicesArrayList!!)

        listView = ListView(this)
        listView!!.adapter = adapter
        listView!!.onItemClickListener = this

        btn_Scan = findViewById<View>(R.id.btn_scan) as Button
        (findViewById<View>(R.id.scrollView) as ScrollView).addView(listView)
        findViewById<View>(R.id.btn_scan).setOnClickListener(this)
    }

    override fun onStart() {
        super.onStart()

        registerReceiver(
            mBTStateUpdateReceiver,
            IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED)
        )
    }

    override fun onResume() {
        super.onResume()

        //        registerReceiver(mBTStateUpdateReceiver, new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED));
    }

    override fun onPause() {
        super.onPause()

        //        unregisterReceiver(mBTStateUpdateReceiver);
        stopScan()
    }

    override fun onStop() {
        super.onStop()

        unregisterReceiver(mBTStateUpdateReceiver)
        stopScan()
    }

    public override fun onDestroy() {
        super.onDestroy()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        // Check which request we're responding to
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_ENABLE_BT) {
            // Make sure the request was successful
            if (resultCode == Activity.RESULT_OK) {
                //                Utils.toast(getApplicationContext(), "Thank you for turning on Bluetooth");
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Utils.toast(applicationContext, "Please turn on Bluetooth")
            }
        } else if (requestCode == BTLE_SERVICES) {
            // Do something
        }
    }

    override fun onItemClick(parent: AdapterView<*>, view: View, position: Int, id: Long) {
        val context = view.context

        //        Utils.toast(context, "List Item clicked");

        // do something with the text views and start the next activity.

        stopScan()

        val name = mBTDevicesArrayList!![position].name
        val address = mBTDevicesArrayList!![position].address

        val intent = Intent(this, Activity_BTLE_Services::class.java)
        intent.putExtra(Activity_BTLE_Services.EXTRA_NAME, name)
        intent.putExtra(Activity_BTLE_Services.EXTRA_ADDRESS, address)
        startActivityForResult(intent, BTLE_SERVICES)
    }

    override fun onClick(v: View) {

        when (v.id) {

            R.id.btn_scan -> {
                Utils.toast(applicationContext, "Scan Button Pressed")

                if (!mBTLeScanner!!.isScanning) {
                    startScan()
                } else {
                    stopScan()
                }
            }
            else -> {
            }
        }

    }

    fun addDevice(device: BluetoothDevice, rssi: Int) {

        val address = device.address
        if (!mBTDevicesHashMap!!.containsKey(address)) {
            val btleDevice = BTLE_Device(device)
            btleDevice.rssi =rssi

            mBTDevicesHashMap!![address] = btleDevice
            mBTDevicesArrayList!!.add(btleDevice)
        } else {
            mBTDevicesHashMap!![address]!!.rssi =rssi
        }

        adapter!!.notifyDataSetChanged()
    }

    fun startScan() {
        btn_Scan!!.text = "Scanning..."

        mBTDevicesArrayList!!.clear()
        mBTDevicesHashMap!!.clear()

        mBTLeScanner!!.start()
    }

    fun stopScan() {
        btn_Scan!!.text = "Scan Again"

        mBTLeScanner!!.stop()
    }

    companion object {
        private val TAG = MainActivity::class.java.simpleName

        val REQUEST_ENABLE_BT = 1
        val BTLE_SERVICES = 2
    }
}
