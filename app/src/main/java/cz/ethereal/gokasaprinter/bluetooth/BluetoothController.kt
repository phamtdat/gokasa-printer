package cz.ethereal.gokasaprinter.bluetooth

import android.annotation.SuppressLint
import android.app.Activity
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.util.Log
import android.widget.Toast
import cz.ethereal.gokasaprinter.GOKASA_PRINTER_DEBUG
import cz.ethereal.gokasaprinter.R
import cz.ethereal.gokasaprinter.data.BluetoothDeviceData
import cz.ethereal.gokasaprinter.data.toBluetoothDeviceDomain
import java.io.IOException
import java.util.Timer
import java.util.TimerTask
import java.util.UUID

class BluetoothController(private val context: Context) {

    private val applicationUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
    private var connectionThread: ConnectionThread? = null

    private val bluetoothManager by lazy {
        context.getSystemService(BluetoothManager::class.java)
    }
    private val bluetoothAdapter by lazy {
        bluetoothManager?.adapter
    }

    fun getBluetoothStatus(): String {
        if (bluetoothAdapter == null) {
            return "unavailable"
        }
        if (!bluetoothAdapter!!.isEnabled) {
            return "disabled"
        }
        return "enabled"
    }

    @SuppressLint("MissingPermission")
    fun getBluetoothDevices(): List<BluetoothDeviceData> {
        return bluetoothAdapter
            ?.bondedDevices
            ?.map { it.toBluetoothDeviceDomain() }!!
            .toList()
            .sortedBy { it.name }
    }

    @SuppressLint("MissingPermission")
    fun connectToSelectedDeviceAndSendData(
        selectedBluetoothDevice: BluetoothDeviceData?,
        data: String,
        onSendDataComplete: () -> Unit,
        onConnectFailed: () -> Unit,
    ) {
        val bluetoothDevice = bluetoothAdapter
            ?.bondedDevices
            ?.find { it.address == selectedBluetoothDevice?.address }

        if (bluetoothDevice == null) {
            Log.d(GOKASA_PRINTER_DEBUG, "selected bluetooth device not found in paired device list")
            onConnectFailed()
            Toast.makeText(context, context.getString(R.string.device_not_found, selectedBluetoothDevice?.name), Toast.LENGTH_SHORT).show()
            return
        }

        connectionThread = ConnectionThread(
            bluetoothDevice,
            data,
            onConnectComplete = { isSuccess ->
                Log.d(GOKASA_PRINTER_DEBUG, "bluetooth socket connect: $isSuccess")
                (context as Activity).runOnUiThread {
                    if (!isSuccess) onConnectFailed()
                }
            },
            onSocketClose = {
                Log.d(GOKASA_PRINTER_DEBUG, "bluetooth socket closed")
                (context as Activity).runOnUiThread {
                    onSendDataComplete()
                }
            },
        )
        connectionThread?.start()
    }


    @SuppressLint("MissingPermission")
    private inner class ConnectionThread(
        private val device: BluetoothDevice,
        private val data: String,
        private val onConnectComplete: (isSuccess: Boolean) -> Unit,
        private val onSocketClose: () -> Unit,
    ) : Thread() {

        private var bluetoothSocket: BluetoothSocket? = createSocket()

        private fun createSocket(): BluetoothSocket? {
            var socket: BluetoothSocket? = null
            try {
                Log.d(GOKASA_PRINTER_DEBUG, "creating socket")
                socket = device.createRfcommSocketToServiceRecord(applicationUUID)
            } catch (e: IOException) {
                Log.d(GOKASA_PRINTER_DEBUG, "cannot create socket: ${e.message}")
            }
            return socket
        }

        override fun run() {
            super.run()
            Log.d(GOKASA_PRINTER_DEBUG, "canceling bluetooth discovery")
            bluetoothAdapter?.cancelDiscovery()
            var isConnectSuccess = false

            try {
                bluetoothSocket?.connect()
                isConnectSuccess = true
            } catch (e: Exception) {
                Log.d(GOKASA_PRINTER_DEBUG, "cannot connect socket: ${e.message}")
            }
            onConnectComplete(isConnectSuccess)
            if (isConnectSuccess) {
                Log.d(GOKASA_PRINTER_DEBUG, "writing to outputStream: $data")
                bluetoothSocket?.outputStream?.write(data.toByteArray())
                bluetoothSocket?.outputStream?.flush()

                Timer().schedule(object : TimerTask() {
                    override fun run() {
                        Log.d(GOKASA_PRINTER_DEBUG, "closing outputStream")
                        bluetoothSocket?.outputStream?.close()
                        Log.d(GOKASA_PRINTER_DEBUG, "closing socket")
                        bluetoothSocket?.close()
                        onSocketClose()
                    }
                }, 1000)
            }
        }
    }
}

