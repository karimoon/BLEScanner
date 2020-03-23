package com.karim.blescanner

import android.app.AlertDialog
import android.app.Dialog
import android.app.DialogFragment
import android.bluetooth.BluetoothGattCharacteristic
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText


class Dialog_BTLE_Characteristic : androidx.fragment.app.DialogFragment(), DialogInterface.OnClickListener {

    private var title: String? = null
    private var service: Service_BTLE_GATT? = null
    private var characteristic: BluetoothGattCharacteristic? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        //return super.onCreateDialog(savedInstanceState)

        val builder = AlertDialog.Builder(activity)
        val inflater = activity!!.layoutInflater

        builder.setView(inflater.inflate(R.layout.dialog_btle_characteristic, null))
            .setNegativeButton("Cancel", this).setPositiveButton("Send", this)
        builder.setTitle(title)

        return builder.create()
    }

    /*override fun onCreateDialog(savedInstanceState: Bundle): Dialog {
        val builder = AlertDialog.Builder(activity)
        val inflater = activity.layoutInflater

        builder.setView(inflater.inflate(R.layout.dialog_btle_characteristic, null))
            .setNegativeButton("Cancel", this).setPositiveButton("Send", this)
        builder.setTitle(title)

        return builder.create()
    }*/

    override fun onClick(dialog: DialogInterface, which: Int) {

        // Find a way to check which button as pressed cancel or ok
        //            Utils.toast(activity.getApplicationContext(), "Button " + Integer.toString(which) + " Pressed");

        val edit = (dialog as AlertDialog).findViewById<View>(R.id.et_submit) as EditText

        when (which) {
            -2 -> {
            }
            -1 ->
                // okay button pressed
                if (service != null) {
                    characteristic!!.setValue(edit.text.toString())
                    service!!.writeCharacteristic(characteristic!!)
                }
            else -> {
            }
        }// cancel button pressed
    }

    fun setTitle(title: String) {
        this.title = title
    }

    fun setService(service: Service_BTLE_GATT) {
        this.service = service
    }

    fun setCharacteristic(characteristic: BluetoothGattCharacteristic) {
        this.characteristic = characteristic
    }
}
