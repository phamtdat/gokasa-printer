package cz.gokasa.gokasaprinter.support

import android.Manifest
import android.bluetooth.BluetoothDevice
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import cz.gokasa.gokasaprinter.R
import kotlin.collections.ArrayList
import android.bluetooth.BluetoothAdapter
import android.content.IntentFilter
import android.content.Intent
import android.content.BroadcastReceiver
import android.os.Build
import android.util.Log
import android.Manifest.permission
import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.content.pm.PackageManager
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat


class ScanForDevicesTaskFragment : Fragment() {

    private val btAdapter = BluetoothAdapter.getDefaultAdapter()

    private var listener: DevicesScanListener? = null

    private var _scanIsRunning: Boolean = false
    val taskIsRunning: Boolean
        get() {
            return _scanIsRunning
        }

//    private var _devices: ArrayList<String> = ArrayList()
//    val devices: ArrayList<String>
//        get() {
//            return _devices
//        }

    private var _btDevices: ArrayList<BluetoothDevice> = ArrayList()
    val btDevices: ArrayList<BluetoothDevice>
        get() {
            return _btDevices
        }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            if (BluetoothAdapter.ACTION_DISCOVERY_STARTED == action) {
                _scanIsRunning = true
                listener?.onScanStarted()
                Log.d("DISCOVERY", "started")
            } else if (BluetoothDevice.ACTION_FOUND == action) {
                // Discovery has found a device. Get the BluetoothDevice object
                val device = intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
                _btDevices.add(device)
                Log.d("DISCOVERY", "found " + device.name)
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED == action) {
                listener?.onScanFinished(_btDevices)
                _scanIsRunning = false
                Log.d("DISCOVERY", "finished")
            }
        }
    }

    val MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1001

    interface DevicesScanListener {
        fun onScanStarted()
        fun onScanFinished(result: ArrayList<BluetoothDevice>)
        fun popup(message: String)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.retainInstance = true
        scan()
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        listener = activity as DevicesScanListener?
        // Register for broadcasts when a device is discovered.
        val filter = IntentFilter(BluetoothDevice.ACTION_FOUND)
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED)
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)
        activity?.registerReceiver(receiver, filter)
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
        activity?.unregisterReceiver(receiver)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (btAdapter.isDiscovering) {
            btAdapter.cancelDiscovery()
            _scanIsRunning = false
        }
        try {
            activity?.unregisterReceiver(receiver)
        } catch (e: Exception) {
            Log.e("ACTION_FOUND_REC", "already unregistered")
        }
    }

    fun scan() {
        if (!_scanIsRunning) {
            //check if app has BT tech and if BT is turned on
            if (btAdapter == null) {
                // Device does not support Bluetooth
                listener?.popup(getResources().getString(R.string.no_bt_tech))
                return
            } else {
                if (!btAdapter.isEnabled) {
                    // Bluetooth is not enabled
                    //todo: offer user to go to Bluetooth settings
                    listener?.popup(getResources().getString(R.string.turn_on_bt))
                    return
                }
            }

            //val accessFineLocation = activity?.checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (ContextCompat.checkSelfPermission(activity!!, Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
                    // Permission is not granted, ask for permission
                    Log.d("REQUEST_PERMISSION", "executing")
                    if(shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
                        Log.d("REQUEST_PERMISSION", "should show reason")
                        //todo show popup reason
                        requestPermissions(
                                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                                MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION)
                    } else {
                        Log.d("REQUEST_PERMISSION", "no reason")
                        requestPermissions(
                                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                                MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION)
                    }
                    return
                }
            }

            _btDevices = ArrayList()
            btAdapter.startDiscovery()

        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    _btDevices = ArrayList()
                    btAdapter.startDiscovery()
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Log.d("PERMISSION", "Not granted")
                    listener?.popup("Cannot connect to printer without this permission.")
                }
            }
        }
    }
}