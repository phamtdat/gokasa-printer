package cz.ethereal.gokasaprinter

import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import java.util.*
import kotlin.concurrent.schedule
import android.bluetooth.*
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.*
import java.io.IOException

class MainActivity : AppCompatActivity() {

    private val defaultPrinterName = ""

    private var mBluetoothDevice: BluetoothDevice? = null
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

        setup()
        listBluetoothDevices()
        catchIntent()
    }
    private fun setup() {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        sharedPreference = getSharedPreferences("GOKASA_PRINTER", Context.MODE_PRIVATE)
        testPrintButton = findViewById(R.id.testPrintButton)
        printerSpinner = findViewById(R.id.printerSpinner)
        scanButton = findViewById(R.id.scanButton)

        testPrintButton.setOnClickListener {
            val printerName = getPreference("printerName")
            sendToPrinter(printerName, "This is a sample message\nThis is another line\n\nAnd another line\n\n\n\n")
        }
        scanButton.setOnClickListener {
            listBluetoothDevices()
        }
    }

    private fun catchIntent() {
        if (intent.data != null) {
            val intentUri: Uri? = intent?.data
            val savedPrinterName = getPreference("printerName")
            val printData: String = intentUri?.getQueryParameter("data").toString()
            //Log.e(TAG, printData)
            sendToPrinter(savedPrinterName, printData)
            exitApp(1000)
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

    private fun exitApp(delay: Long) {
        Timer("Closing", false).schedule(delay) {
            mBluetoothSocket!!.close()
            finish()
        }
    }

    private fun sendToPrinter(printerName: String, data: String) {
        if (!setBluetoothDevice(printerName)) {
            return
        }
        val os = mBluetoothSocket!!.outputStream
        os.write(data.toByteArray())
        os.flush()
        mBluetoothSocket!!.close()
        //Log.e(TAG, "Wrote to Bluetooth socket output stream")
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
        val printerNames = arrayListOf<String>()
        var defaultSelectionIndex = 0
        for ((index, device) in pairedDevices.withIndex()) {
            //Log.e(TAG, "Found paired device ***$index ${device!!.name}***")
            //Log.e(TAG, "Saved device name is ***${getPreference("printerName")}***")
            printerNames.add(device!!.name)
            if (device.name == getPreference("printerName")) {
                defaultSelectionIndex = index
            }
        }

        val arrayAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, printerNames)
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        printerSpinner.adapter = arrayAdapter
        printerSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                val printerName = printerNames[position]
                setPreference("printerName", printerName)
                toast("${getString(R.string.bt_selected)} ${printerNames[position]}")
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Code to perform some action when nothing is selected
            }
        }
        if (printerNames.size > 0) {
            printerSpinner.setSelection(defaultSelectionIndex)
        }

    }

    private fun setBluetoothDevice(bluetoothDeviceName: String): Boolean {
        if (!checkBluetoothState()) {
            return false
        }
        val pairedDevices = mBluetoothAdapter!!.bondedDevices
        for (device in pairedDevices) {
            if (device!!.name == bluetoothDeviceName) {
                mBluetoothDevice = device
                //Log.e(TAG,"Bluetooth Device $bluetoothDeviceName Setup OK")
                return setBluetoothSocket()
            }
        }
        toast("${getString(R.string.bt_device_not_found)} $bluetoothDeviceName")
        return false
    }

    private fun setBluetoothSocket(): Boolean {
        try {
            mBluetoothSocket = mBluetoothDevice?.createRfcommSocketToServiceRecord(applicationUUID)
            //mBluetoothAdapter!!.cancelDiscovery()
            mBluetoothSocket!!.connect()
        } catch (eConnectException: IOException) {
            //Log.e(TAG, "Could not connect to socket", eConnectException)
            toast(getString(R.string.bt_bad_device))
            mBluetoothSocket!!.close()
            return false
        }
        //Log.e(TAG, "Bluetooth Socket Setup OK")
        return true
    }

    private fun toast(msg: String) {
        Toast.makeText(this@MainActivity, msg, Toast.LENGTH_SHORT).show()
    }

    companion object {
        private const val TAG = "LOG"
    }
}
