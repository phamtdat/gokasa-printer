package cz.gokasa.gokasaprinter.support

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import cz.gokasa.gokasaprinter.R

class TestAdapter : RecyclerView.Adapter<TestAdapter.ViewHolder>() {

    private var dummyDevices: ArrayList<String>? = null

    init {
        this.dummyDevices = ArrayList()
    }

    fun setData(dummyDevices: ArrayList<String>) {
        this.dummyDevices = dummyDevices
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.bt_device_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(dummyDevices!![position])
    }

    override fun getItemCount(): Int {
        return dummyDevices!!.size
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        private var tvDeviceName: TextView? = null

        init {
            tvDeviceName = view.findViewById(R.id.tv_device_name)
        }

        internal fun bind(dummyDevice: String) {
            tvDeviceName?.text = dummyDevice
        }
    }

}