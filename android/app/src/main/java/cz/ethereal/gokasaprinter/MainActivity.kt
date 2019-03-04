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
import android.support.constraint.ConstraintLayout
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import java.io.IOException

class MainActivity : AppCompatActivity() {

    private var mBluetoothDevice: BluetoothDevice? = null
    private var mBluetoothAdapter: BluetoothAdapter? = null
    private var mBluetoothSocket: BluetoothSocket? = null

    private lateinit var sharedPreference: SharedPreferences
    private lateinit var printerNameInput: EditText
    private lateinit var savePrinterNameButton: Button
    private lateinit var testPrintButton: Button

    private val applicationUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setup()
        listBluetoothDevices()
        catchIntent()
    }

    private fun setup() {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()

        sharedPreference = getSharedPreferences("GOKASA_PRINTER", Context.MODE_PRIVATE)
        printerNameInput = findViewById(R.id.printerNameInput)
        printerNameInput.setText(getPreference("printerName"))
        savePrinterNameButton = findViewById(R.id.savePrinterNameButton)
        testPrintButton = findViewById(R.id.testPrintButton)

        savePrinterNameButton.setOnClickListener {
            val printerName = printerNameInput.text.toString()
            setPreference("printerName", printerName)
        }
        testPrintButton.setOnClickListener {
            val printerName = printerNameInput.text.toString()
            sendToPrinter(printerName, "This is a sample message\nThis is another line\n\nAnd another line\n\n")
        }
    }

    private fun catchIntent() {
        if (intent.data != null) {
            val intentUri: Uri? = intent?.data
            Log.e(TAG, "Intent called")
            val savedPrinterName = getPreference("printerName")
            val data: String = intentUri?.getQueryParameter("data").toString()
            sendToPrinter(savedPrinterName, data)
            exitApp(1000)
        }
    }

    private fun setPreference(key: String, value: String) {
        var editor = sharedPreference.edit()
        editor?.putString(key, value)
        editor?.commit()
    }

    private fun getPreference(key: String?): String {
        return sharedPreference.getString(key, "PTP-II").toString()
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
        if (!setBluetoothDevice(printerName)) {
            return
        }
        if (!setBluetoothSocket()) {
            return
        }
        val os = mBluetoothSocket!!.outputStream
        os.write(data.toByteArray())
    }

    private fun checkBluetoothState(): Boolean {
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
        return true
    }

    private fun listBluetoothDevices() {
        if (!checkBluetoothState()) {
            return
        }
        //val pairedDevices = mBluetoothAdapter!!.bondedDevices
        /*for (device in pairedDevices) {
            val constraintLayout = findViewById<ConstraintLayout>(R.id.constraintLayout)
            val button = Button(this)
            button.layoutParams = ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT)
            button.text = "device.name"
            button.setOnClickListener{
                toast("You have selected device.name $i" )
            }
            constraintLayout.addView(button)

        }*/
    }

    private fun setBluetoothDevice(bluetoothDeviceName: String): Boolean {
        if (!checkBluetoothState()) {
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

    private fun closeSocket(nOpenSocket: BluetoothSocket) {
        try {
            nOpenSocket.close()
            Log.d(TAG, "SocketClosed")
        } catch (ex: IOException) {
            Log.d(TAG, "CouldNotCloseSocket")
        }

    }

    private fun toast(msg: String) {
        Toast.makeText(this@MainActivity, msg, Toast.LENGTH_SHORT).show()
    }

    companion object {
        private const val TAG = "LOG"
    }
}
