package cz.ethereal.gokasaprinter.data

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice

@SuppressLint("MissingPermission")
fun BluetoothDevice.toBluetoothDeviceDomain(): BluetoothDeviceData {
    return BluetoothDeviceData(
        name = name,
        address = address
    )
}
