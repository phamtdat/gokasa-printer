package cz.ethereal.gokasaprinter

import android.net.Uri
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AppCompatActivity
import android.util.Log
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import kotlin.concurrent.schedule
import android.bluetooth.*
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import java.io.IOException


class MainActivity : AppCompatActivity() {

    private var sharedPreference: SharedPreferences? = null
    private var mBluetoothDevice: BluetoothDevice? = null
    private var mBluetoothAdapter: BluetoothAdapter? = null
    private var mBluetoothSocket: BluetoothSocket? = null
    private val applicationUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_home -> {
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_dashboard -> {
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_notifications -> {
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

        bindEvents()

        if (intent.data != null) {
            val intentUri: Uri? = intent?.data
            Log.e(TAG, "Intent called")
            val savedPrinterName = getPreference("printerName")
            val data: String = intentUri?.getQueryParameter("data").toString()
            sendToPrinter(savedPrinterName, data)
            exitApp(1000)
        }
    }

    private fun bindEvents() {
        sharedPreference =  getSharedPreferences("GOKASA_PRINTER", Context.MODE_PRIVATE)

        val printerNameInput = findViewById<EditText>(R.id.printerNameInput)
        val savedPrinterName = getPreference("printerName")
        printerNameInput.setText(savedPrinterName)

        val savePrinterNameButton = findViewById<Button>(R.id.savePrinterNameButton)
        savePrinterNameButton.setOnClickListener {
            val printerName = printerNameInput.text.toString()
            setPreference("printerName", printerName)
        }

        val testPrintButton = findViewById<Button>(R.id.testPrintButton)
        testPrintButton.setOnClickListener {
            val printerName = printerNameInput.text.toString()
            sendToPrinter(printerName,"This is a sample message\nThis is another line\n\nAnd another line\n\n")

        }
    }

    private fun setPreference(key: String, value: String) {
        var editor = sharedPreference?.edit()
        editor?.putString(key, value)
        editor?.commit()
    }

    private fun getPreference(key: String): String {
        return sharedPreference?.getString(key, "").toString()
    }

    private fun exitApp(delay: Long) {
        Timer("Closing", false).schedule(delay) {
            try {
                if (mBluetoothSocket != null) mBluetoothSocket!!.close()
            } catch (e: Exception) {
                Log.e(TAG, "Problem with Bluetooth socket ", e)
            }
            finish()
        }
    }

    private fun sendToPrinter(printerName: String, data: String) {
        if (!setBlueToothDevice(printerName)) {
            return
        }
        if (!setBluetoothSocket()) {
            return
        }
        val os = mBluetoothSocket!!.outputStream
        os.write(data.toByteArray())
    }

    private fun setBlueToothDevice(bluetoothDeviceName: String): Boolean {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        if (mBluetoothAdapter == null) {
            toast("No bluetooth adapter available!")
            return false
        }
        if (!mBluetoothAdapter!!.isEnabled) {
            toast("Please activate bluetooth!")
            val enableBluetooth = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableBluetooth, 0)
            return false
        }
        val pairedDevices = mBluetoothAdapter!!.bondedDevices
        for (device in pairedDevices) {
            if (device!!.name == bluetoothDeviceName) {
                mBluetoothDevice = device
                toast("Bluetooth Device " + bluetoothDeviceName + " Setup OK")
                return true
            }
        }
        return false
    }

    private fun setBluetoothSocket(): Boolean {
        try {
            mBluetoothSocket = mBluetoothDevice?.createRfcommSocketToServiceRecord(applicationUUID)
            mBluetoothAdapter!!.cancelDiscovery()
            mBluetoothSocket!!.connect()
        } catch (eConnectException: IOException) {
            Log.e(TAG, "CouldNotConnectToSocket", eConnectException)
            closeSocket(mBluetoothSocket!!)
            return false
        }
        toast("Bluetooth Socket Setup OK")
        return true
    }

    private fun toast(msg: String) {
        Toast.makeText(this@MainActivity, msg, Toast.LENGTH_SHORT).show()
    }

    private fun closeSocket(nOpenSocket: BluetoothSocket) {
        try {
            nOpenSocket.close()
            Log.d(TAG, "SocketClosed")
        } catch (ex: IOException) {
            Log.d(TAG, "CouldNotCloseSocket")
        }

    }

    companion object {
        private const val TAG = "LOG"
    }
}
