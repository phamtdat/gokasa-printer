package cz.gokasa.gokasaprinter.support

import android.bluetooth.BluetoothDevice
import android.graphics.Color
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import cz.gokasa.gokasaprinter.R
import java.util.ArrayList

class BluetoothDeviceListAdapter : RecyclerView.Adapter<BluetoothDeviceListAdapter.ViewHolder>() {

    private var btDevices: ArrayList<BluetoothDevice>? = null

    init {
        this.btDevices = ArrayList()
    }

    fun setData(btDevices: ArrayList<BluetoothDevice>) {
        this.btDevices = btDevices
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.bt_device_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(btDevices!![position])
    }

    override fun getItemCount(): Int {
        return btDevices!!.size
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        private var tvDeviceName: TextView? = null
        private var tvDeviceAddress: TextView? = null
        private var tvDeviceClass: TextView? = null

        init {
            tvDeviceName = view.findViewById(R.id.tv_device_name)
            tvDeviceAddress = view.findViewById(R.id.tv_device_address)
            tvDeviceClass = view.findViewById(R.id.tv_device_class)
        }

        internal fun bind(btDevice: BluetoothDevice) {
            tvDeviceName?.text = btDevice.name
            tvDeviceAddress?.text = btDevice.address
            tvDeviceClass?.text = BtDeviceUtil.getStringDeviceClass(btDevice.bluetoothClass.deviceClass)
        }
    }

}
