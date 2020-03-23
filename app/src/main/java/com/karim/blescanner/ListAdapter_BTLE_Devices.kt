package com.karim.blescanner

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

import java.util.ArrayList

class ListAdapter_BTLE_Devices(
    internal var activity: Activity,
    internal var layoutResourceID: Int,
    internal var devices: ArrayList<BTLE_Device>
) : ArrayAdapter<BTLE_Device>(activity.applicationContext, layoutResourceID, devices) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var convertView = convertView

        if (convertView == null) {
            val inflater =
                activity.applicationContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            convertView = inflater.inflate(layoutResourceID, parent, false)
        }

        val device = devices[position]
        val name = device.name
        val address = device.address
        val rssi = device.rssi

        var tv: TextView? = null

        tv = convertView!!.findViewById<View>(R.id.tv_name) as TextView
        if (name != null && name.length > 0) {
            tv.text = device.name
        } else {
            tv.text = "No Name"
        }

        tv = convertView.findViewById<View>(R.id.tv_rssi) as TextView
        tv.text = "RSSI: " + Integer.toString(rssi)

        tv = convertView.findViewById<View>(R.id.tv_macaddr) as TextView
        if (address != null && address.length > 0) {
            tv.text = device.address
        } else {
            tv.text = "No Address"
        }

        return convertView
    }
}
