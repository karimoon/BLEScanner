package com.karim.blescanner

import android.app.Activity
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattService
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.TextView

import java.util.ArrayList
import java.util.HashMap


class ListAdapter_BTLE_Services(
    private val activity: Activity, private val services_ArrayList: ArrayList<BluetoothGattService>,
    private val characteristics_HashMap: HashMap<String, ArrayList<BluetoothGattCharacteristic>>
) : BaseExpandableListAdapter() {

    override fun getGroupCount(): Int {
        return services_ArrayList.size
    }

    override fun getChildrenCount(groupPosition: Int): Int {
        return characteristics_HashMap[services_ArrayList[groupPosition].uuid.toString()]!!.size
    }

    override fun getGroup(groupPosition: Int): Any {
        return services_ArrayList[groupPosition]
    }

    override fun getChild(groupPosition: Int, childPosition: Int): Any {

        return characteristics_HashMap[services_ArrayList[groupPosition].uuid.toString()]!![childPosition]
    }

    override fun getGroupId(groupPosition: Int): Long {
        return 0
    }

    override fun getChildId(groupPosition: Int, childPosition: Int): Long {
        return 0
    }

    override fun hasStableIds(): Boolean {
        return false
    }

    override fun getGroupView(
        groupPosition: Int,
        isExpanded: Boolean,
        convertView: View?,
        parent: ViewGroup
    ): View {
        var convertView = convertView

        val bluetoothGattService = getGroup(groupPosition) as BluetoothGattService

        val serviceUUID = bluetoothGattService.uuid.toString()
        if (convertView == null) {
            val inflater =
                activity.applicationContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            convertView = inflater.inflate(R.layout.btle_service_list_item, null)
        }

        val tv_service = convertView!!.findViewById<View>(R.id.tv_service_uuid) as TextView
        tv_service.text = "S: $serviceUUID"

        return convertView
    }

    override fun getChildView(
        groupPosition: Int,
        childPosition: Int,
        isLastChild: Boolean,
        convertView: View?,
        parent: ViewGroup
    ): View {
        var convertView = convertView

        val bluetoothGattCharacteristic =
            getChild(groupPosition, childPosition) as BluetoothGattCharacteristic

        val characteristicUUID = bluetoothGattCharacteristic.uuid.toString()
        if (convertView == null) {
            val inflater =
                activity.applicationContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            convertView = inflater.inflate(R.layout.btle_characteristics_list_item, null)
        }

        val tv_service = convertView!!.findViewById<View>(R.id.tv_characteristic_uuid) as TextView
        tv_service.text = "C: $characteristicUUID"

        val properties = bluetoothGattCharacteristic.properties

        val tv_property = convertView.findViewById<View>(R.id.tv_properties) as TextView
        val sb = StringBuilder()

        if (Utils.hasReadProperty(properties) != 0) {
            sb.append("R")
        }

        if (Utils.hasWriteProperty(properties) != 0) {
            sb.append("W")
        }

        if (Utils.hasNotifyProperty(properties) != 0) {
            sb.append("N")
        }

        tv_property.text = sb.toString()

        val tv_value = convertView.findViewById<View>(R.id.tv_value) as TextView

        val data = bluetoothGattCharacteristic.value
        if (data != null) {
            tv_value.text = "Value: " + Utils.hexToString(data)
        } else {
            tv_value.text = "Value: ---"
        }

        return convertView
    }

    override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean {
        return true
    }
}
