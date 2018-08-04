package cz.gokasa.gokasaprinter.support

import android.bluetooth.BluetoothDevice
import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import android.support.v4.app.Fragment
import cz.gokasa.gokasaprinter.R
import java.util.*
import kotlin.collections.ArrayList


class ScanForDevicesTaskFragment : Fragment() {

    private var listener: DevicesScanListener? = null

    private var _taskIsRunning: Boolean = false
    val taskIsRunning: Boolean
        get() {
            return _taskIsRunning
        }

    private var _devices: ArrayList<String> = ArrayList()
    val devices: ArrayList<String>
        get() {
            return _devices
        }

    interface DevicesScanListener {
        fun onPreExecute()
        fun onPostExecute(result: ArrayList<String>)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.retainInstance = true
        ScanForDevicesTask().execute()
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        listener = activity as DevicesScanListener?
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    fun scan() {
        if(!_taskIsRunning) {
            _devices = ArrayList()
            ScanForDevicesTask().execute()
        }
    }

    //TODO: change String to BluetoothDevice
    inner class ScanForDevicesTask : AsyncTask<Void, Void, ArrayList<String>>() {

        //private var progressBar = ProgressBar(this@ScanForDevicesTaskFragment.context,null,android.R.attr.progressBarStyleSmall)

        override fun onPreExecute() {
            super.onPreExecute()

            //TODO check if app has all permissions and if BT is turned on
            this@ScanForDevicesTaskFragment._taskIsRunning = true
            listener?.onPreExecute()
        }

        override fun doInBackground(vararg params: Void?): ArrayList<String> {
            //TODO scan for BT devices
            Thread.sleep(5000)
            val list = Arrays.asList(*resources.getStringArray(R.array.dummy_devices))
            return ArrayList(list)
            //return ArrayList()
        }

        override fun onPostExecute(result: ArrayList<String>) {
            super.onPostExecute(result)
            this@ScanForDevicesTaskFragment._taskIsRunning = false
            this@ScanForDevicesTaskFragment._devices = result
            listener?.onPostExecute(result)
        }
    }
}