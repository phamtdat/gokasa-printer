package cz.ethereal.gokasaprinter

import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import java.util.*
import kotlin.concurrent.schedule
import android.bluetooth.*
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.view.View
import android.widget.*
import java.io.IOException

class MainActivity : AppCompatActivity() {

    private val defaultPrinterName = ""

    private var selectedOnce = false
    private var mBluetoothAdapter: BluetoothAdapter? = null
    private var mBluetoothSocket: BluetoothSocket? = null

    private lateinit var sharedPreference: SharedPreferences
    private lateinit var testPrintButton: Button
    private lateinit var printerSpinner: Spinner
    private lateinit var scanButton: Button

    private val applicationUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.setIcon(R.mipmap.logo)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        sharedPreference = getSharedPreferences("GOKASA_PRINTER", Context.MODE_PRIVATE)
        testPrintButton = findViewById(R.id.testPrintButton)
        printerSpinner = findViewById(R.id.printerSpinner)
        scanButton = findViewById(R.id.scanButton)

        testPrintButton.setOnClickListener {
            sendToPrinter("This is a sample message\nThis is another line\n\nAnd another line\n\n\n\n.\nGokasa Printer\n.")
        }
        scanButton.setOnClickListener {
            listBluetoothDevices()
        }

        listBluetoothDevices()

        if (intent.data != null) {
            val intentUri: Uri? = intent.data
            val printData: String = intentUri?.getQueryParameter("data").toString()
//            Log.d(TAG, printData)
            sendToPrinter(printData)
            Timer("Closing", false).schedule(2000) {
                mBluetoothSocket!!.close()
                finish()
            }
        }
    }

    private fun setPreference(key: String, value: String) {
        val editor = sharedPreference.edit()
        editor?.putString(key, value)
        editor?.apply()
    }

    private fun getPreference(key: String): String {
        return sharedPreference.getString(key, defaultPrinterName)!!
    }

    private fun sendToPrinter(data: String) {
        if (!checkBluetoothState()) {
            return
        }
        val printerName = getPreference("printerName")
        val printerAddress = getPreference("printerAddress")
        val pairedDevices = mBluetoothAdapter!!.bondedDevices
        for (device in pairedDevices) {
//            Log.d(TAG, device.name);
//            Log.d(TAG, device.address);
//            Log.d(TAG, device.bluetoothClass.toString())
//            Log.d(TAG, device.bondState.toString())
//            Log.d(TAG, "------------------------")
            if (device.name == printerName && device.address == printerAddress) {
                //Log.d(TAG,"Bluetooth Device $bluetoothDeviceName Setup OK")

                try {
                    mBluetoothSocket = device.createRfcommSocketToServiceRecord(applicationUUID)
                    //mBluetoothAdapter!!.cancelDiscovery()
                    mBluetoothSocket!!.connect()
                    val os = mBluetoothSocket!!.outputStream
                    os.write(data.toByteArray())
                    os.flush()
                    mBluetoothSocket!!.close()
                } catch (eConnectException: IOException) {
                    //Log.d(TAG, "Could not connect to socket", eConnectException)
                    toast(getString(R.string.bt_bad_device))
                    mBluetoothSocket!!.close()
                    return
                }
                //Log.d(TAG, "Bluetooth Socket Setup OK")
                return
            }
        }
        toast("${getString(R.string.bt_device_not_found)} $printerName $printerAddress")
        return
    }

    private fun checkBluetoothState(): Boolean {
        if (mBluetoothAdapter == null) {
            toast(getString(R.string.bt_not_available))
            return false
        }
        if (!mBluetoothAdapter!!.isEnabled) {
            toast(getString(R.string.bt_activate))
            val enableBluetooth = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableBluetooth, 0)
            return false
        }
        return true
    }

    private fun listBluetoothDevices() {
        if (!checkBluetoothState()) {
            return
        }
        val pairedDevices = mBluetoothAdapter!!.bondedDevices
        val btDevices = arrayListOf<BluetoothDevice>()
        val printerName = getPreference("printerName")
        val printerAddress = getPreference("printerAddress")
        var defaultSelectionIndex = 0
        for ((index, device) in pairedDevices.withIndex()) {
            //Log.d(TAG, "Found paired device ***$index ${device!!.name}***")
            //Log.d(TAG, "Saved device name is ***${getPreference("printerName")}***")
            btDevices.add(device)
            if (device.name == printerName && device.address == printerAddress) {
                defaultSelectionIndex = index
            }
        }

        val arrayAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, btDevices.map { it.name })
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        printerSpinner.adapter = arrayAdapter
        printerSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                setPreference("printerName", btDevices[position].name)
                setPreference("printerAddress", btDevices[position].address)
                if (selectedOnce) {
                    toast("${getString(R.string.bt_selected)} ${btDevices[position].name}")
                }
                selectedOnce = true
            }
            override fun onNothingSelected(parent: AdapterView<*>) {
                // Code to perform some action when nothing is selected
            }
        }
        if (btDevices.size > 0) {
            printerSpinner.setSelection(defaultSelectionIndex)
        }
    }

    private fun toast(msg: String) {
        Toast.makeText(this@MainActivity, msg, Toast.LENGTH_SHORT).show()
    }

    companion object {
        private const val TAG = "LOG"
    }
}
