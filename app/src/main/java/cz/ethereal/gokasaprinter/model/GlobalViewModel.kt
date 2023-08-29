package cz.ethereal.gokasaprinter.model

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import cz.ethereal.gokasaprinter.GOKASA_PRINTER_DEBUG
import cz.ethereal.gokasaprinter.R
import cz.ethereal.gokasaprinter.bluetooth.BluetoothController
import cz.ethereal.gokasaprinter.data.BluetoothDeviceData
import cz.ethereal.gokasaprinter.data.PreferenceManager

class GlobalViewModel : ViewModel() {

    val isBluetoothConnectFailed: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>(false)
    }

    fun setIsBluetoothConnectFailed(value: Boolean) {
        isBluetoothConnectFailed.value = value
    }

    val isBluetoothBusy: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>(false)
    }

    private fun setIsBluetoothBusy(value: Boolean) {
        isBluetoothBusy.value = value
    }


    fun print(context: Context, data: String, onSendDataComplete: () -> Unit, onConnectFailed: () -> Unit) {
        Log.d(GOKASA_PRINTER_DEBUG, "data to print: ${data.length} characters")
        setIsBluetoothBusy(true)
        BluetoothController(context).connectToSelectedDeviceAndSendData(
            selectedBluetoothDevice.value!!,
            data,
            onSendDataComplete = onSendDataComplete,
            onConnectFailed = onConnectFailed,
        )
    }

    fun printTestString(context: Context) {
        print(
            context,
            context.getString(R.string.test_print_string),
            onSendDataComplete = {
                setIsBluetoothBusy(false)
            },
            onConnectFailed = {
                setIsBluetoothConnectFailed(true)
                setIsBluetoothBusy(false)
            },
        )
    }

    val bluetoothStatus: MutableLiveData<String> by lazy {
        MutableLiveData<String>("")
    }

    fun setBluetoothStatus(status: String) {
        bluetoothStatus.value = status
    }

    val bluetoothPermissionsGranted: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>(Build.VERSION.SDK_INT < Build.VERSION_CODES.S)
    }

    fun setBluetoothPermissionsGranted(granted: Boolean) {
        bluetoothPermissionsGranted.value = granted
    }


    fun requestBluetoothPermissions(
        bluetoothPermissionLauncher: ActivityResultLauncher<Array<String>>,
        enableBluetoothLauncher: ActivityResultLauncher<Intent>,
    ) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
            enableBluetoothLauncher.launch(
                Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE),
            )
            return
        }
        bluetoothPermissionLauncher.launch(
            arrayOf(
                Manifest.permission.BLUETOOTH_SCAN,
                Manifest.permission.BLUETOOTH_CONNECT,
                Manifest.permission.BLUETOOTH_ADMIN,
            )
        )
    }

    val bluetoothDevices: MutableLiveData<List<BluetoothDeviceData>> by lazy {
        MutableLiveData<List<BluetoothDeviceData>>(emptyList())
    }

    private fun setBluetoothDevices(devices: List<BluetoothDeviceData>) {
        bluetoothDevices.value = devices
    }

    fun updateBluetoothDevices(context: Context, notify: Boolean) {
        val bluetoothDevices = BluetoothController(context).getBluetoothDevices()
        setBluetoothDevices(bluetoothDevices)

        val savedBluetoothDevice = PreferenceManager(context).getSavedBluetoothDevice()
        if (savedBluetoothDevice != null && savedBluetoothDevice.address != selectedBluetoothDevice.value?.address) {
            val savedBluetoothDeviceInDeviceList = bluetoothDevices.find { it.address == savedBluetoothDevice.address }
            if (savedBluetoothDeviceInDeviceList != null) {
                setSelectedBluetoothDevice(savedBluetoothDeviceInDeviceList, context)
            }

        }

        if (notify && bluetoothDevices.isNotEmpty()) {
            Toast.makeText(
                context,
                context.getString(R.string.you_have_n_paired_devices, bluetoothDevices.size.toString()),
                Toast.LENGTH_SHORT,
            ).show()
        }
    }


    val selectedBluetoothDevice: MutableLiveData<BluetoothDeviceData> by lazy {
        MutableLiveData<BluetoothDeviceData>(null)
    }

    fun setSelectedBluetoothDevice(device: BluetoothDeviceData, context: Context) {
        selectedBluetoothDevice.value = device
        val sharedPreference = PreferenceManager(context)
        if (sharedPreference.getSavedBluetoothDevice()?.address != device.address) {
            sharedPreference.setSavedBluetoothDevice(device)
        }
    }

}