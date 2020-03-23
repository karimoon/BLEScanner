package com.karim.blescanner

import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattService
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.View
import android.widget.ExpandableListView
import android.widget.TextView

import androidx.appcompat.app.AppCompatActivity

import java.util.ArrayList
import java.util.HashMap

class Activity_BTLE_Services : AppCompatActivity(), ExpandableListView.OnChildClickListener {

    private var expandableListAdapter: ListAdapter_BTLE_Services? = null
    private var expandableListView: ExpandableListView? = null

    private var services_ArrayList: ArrayList<BluetoothGattService>? = null
    private var characteristics_HashMap: HashMap<String, BluetoothGattCharacteristic>? = null
    private var characteristics_HashMapList: HashMap<String, ArrayList<BluetoothGattCharacteristic>>? =
        null

    private var mBTLE_Service_Intent: Intent? = null
    private var mBTLE_Service: Service_BTLE_GATT? = null
    private var mBTLE_Service_Bound: Boolean = false
    private var mGattUpdateReceiver: BroadcastReceiver_BTLE_GATT? = null

    private var name: String? = null
    private var address: String? = null

    private val mBTLE_ServiceConnection = object : ServiceConnection {

        override fun onServiceConnected(className: ComponentName, service: IBinder) {

            // We've bound to LocalService, cast the IBinder and get LocalService instance
            val binder = service as Service_BTLE_GATT.BTLeServiceBinder
            mBTLE_Service = binder.service
            mBTLE_Service_Bound = true

            if (!mBTLE_Service!!.initialize()) {
                Log.e(TAG, "Unable to initialize Bluetooth")
                finish()
            }

            mBTLE_Service!!.connect(address)

            // Automatically connects to the device upon successful start-up initialization.
            //            mBTLeService.connect(mBTLeDeviceAddress);

            //            mBluetoothGatt = mBTLeService.getmBluetoothGatt();
            //            mGattUpdateReceiver.setBluetoothGatt(mBluetoothGatt);
            //            mGattUpdateReceiver.setBTLeService(mBTLeService);
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            mBTLE_Service = null
            mBTLE_Service_Bound = false

            //            mBluetoothGatt = null;
            //            mGattUpdateReceiver.setBluetoothGatt(null);
            //            mGattUpdateReceiver.setBTLeService(null);
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_btle_services)

        Log.d("oncreate_btle_services" , "-->")

        val intent = intent
        name = intent.getStringExtra(Activity_BTLE_Services.EXTRA_NAME)
        address = intent.getStringExtra(Activity_BTLE_Services.EXTRA_ADDRESS)

        services_ArrayList = ArrayList()
        characteristics_HashMap = HashMap()
        characteristics_HashMapList = HashMap()

        expandableListAdapter = ListAdapter_BTLE_Services(
            this, services_ArrayList!!, characteristics_HashMapList!!
        )

        expandableListView = findViewById<View>(R.id.lv_expandable) as ExpandableListView
        expandableListView!!.setAdapter(expandableListAdapter)
        expandableListView!!.setOnChildClickListener(this)

        (findViewById<View>(R.id.tv_name) as TextView).text = name!! + " Services"
        (findViewById<View>(R.id.tv_address) as TextView).text = address
    }

    override fun onStart() {
        super.onStart()

        mGattUpdateReceiver = BroadcastReceiver_BTLE_GATT(this)
        registerReceiver(mGattUpdateReceiver, Utils.makeGattUpdateIntentFilter())

        mBTLE_Service_Intent = Intent(this, Service_BTLE_GATT::class.java)
        bindService(mBTLE_Service_Intent, mBTLE_ServiceConnection, Context.BIND_AUTO_CREATE)
        startService(mBTLE_Service_Intent)
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onStop() {
        super.onStop()

        unregisterReceiver(mGattUpdateReceiver)
        unbindService(mBTLE_ServiceConnection)
        mBTLE_Service_Intent = null
    }

    override fun onChildClick(
        parent: ExpandableListView,
        v: View,
        groupPosition: Int,
        childPosition: Int,
        id: Long
    ): Boolean {

        val characteristic =
            characteristics_HashMapList!![services_ArrayList!![groupPosition].uuid.toString()]!![childPosition]

        if (Utils.hasWriteProperty(characteristic.properties) != 0) {
            val uuid = characteristic.uuid.toString()

            val dialog_btle_characteristic = Dialog_BTLE_Characteristic()

            dialog_btle_characteristic.setTitle(uuid)
            dialog_btle_characteristic.setService(mBTLE_Service!!)
            dialog_btle_characteristic.setCharacteristic(characteristic)

            dialog_btle_characteristic.show(supportFragmentManager,"Dialog_BTLE_Characteristic")
        } else if (Utils.hasReadProperty(characteristic.properties) != 0) {
            if (mBTLE_Service != null) {
                mBTLE_Service!!.readCharacteristic(characteristic)
            }
        } else if (Utils.hasNotifyProperty(characteristic.properties) != 0) {
            if (mBTLE_Service != null) {
                mBTLE_Service!!.setCharacteristicNotification(characteristic, true)
            }
        }

        return false
    }

    fun updateServices() {

        if (mBTLE_Service != null) {

            services_ArrayList!!.clear()
            characteristics_HashMap!!.clear()
            characteristics_HashMapList!!.clear()

            val servicesList = mBTLE_Service!!.supportedGattServices

            for (service in servicesList!!) {

                services_ArrayList!!.add(service)

                val characteristicsList = service.characteristics
                val newCharacteristicsList = ArrayList<BluetoothGattCharacteristic>()

                for (characteristic in characteristicsList) {
                    characteristics_HashMap!![characteristic.uuid.toString()] = characteristic
                    newCharacteristicsList.add(characteristic)
                }

                characteristics_HashMapList!![service.uuid.toString()] = newCharacteristicsList
            }

            if (servicesList != null && servicesList.size > 0) {
                expandableListAdapter!!.notifyDataSetChanged()
            }
        }
    }

    fun updateCharacteristic() {
        expandableListAdapter!!.notifyDataSetChanged()
    }

    companion object {
        private val TAG = Activity_BTLE_Services::class.java.simpleName

        val EXTRA_NAME = "android.aviles.bletutorial.Activity_BTLE_Services.NAME"
        val EXTRA_ADDRESS = "android.aviles.bletutorial.Activity_BTLE_Services.ADDRESS"
    }
}
