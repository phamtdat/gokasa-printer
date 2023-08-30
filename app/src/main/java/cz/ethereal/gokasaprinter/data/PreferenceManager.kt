package cz.ethereal.gokasaprinter.data

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import cz.ethereal.gokasaprinter.GOKASA_PRINTER_DEBUG
import cz.ethereal.gokasaprinter.GOKASA_PRINTER_SHARED_PREFERENCES
import cz.ethereal.gokasaprinter.SAVED_APP_LANGUAGE
import cz.ethereal.gokasaprinter.SAVED_BLUETOOTH_DEVICE
import cz.ethereal.gokasaprinter.SAVED_BLUETOOTH_PERMISSION_GRANTED

class PreferenceManager(context: Context) {
    private val sharedPreferences = context.getSharedPreferences(GOKASA_PRINTER_SHARED_PREFERENCES, Context.MODE_PRIVATE)
    private val editor = sharedPreferences.edit()
    private val gson = Gson()

    private fun String.put(value: String) {
        editor.putString(this, value)
        editor.commit()
    }

    private fun String.put(value: Boolean) {
        editor.putBoolean(this, value)
        editor.commit()
    }

    private fun String.getString() = sharedPreferences.getString(this, "")!!

    private fun String.getBoolean() = sharedPreferences.getBoolean(this, false)

    fun setSavedBluetoothDevice(bluetoothDevice: BluetoothDeviceData) {
        val jsonString = gson.toJson(bluetoothDevice)
        Log.d(GOKASA_PRINTER_DEBUG, "writing sharedPreference $SAVED_BLUETOOTH_DEVICE $jsonString")
        SAVED_BLUETOOTH_DEVICE.put(jsonString)
    }

    fun getSavedBluetoothDevice(): BluetoothDeviceData? {
        var jsonString = ""
        fun getResult(): BluetoothDeviceData? {
            val preferenceValue = SAVED_BLUETOOTH_DEVICE.getString()
            if (preferenceValue.isNotEmpty()) {
                jsonString = SAVED_BLUETOOTH_DEVICE.getString()
                return gson.fromJson(jsonString, BluetoothDeviceData::class.java)
            }
            return null
        }

        val result = getResult()
        Log.d(GOKASA_PRINTER_DEBUG, "reading sharedPreference $SAVED_BLUETOOTH_DEVICE $jsonString ${result?.name} ${result?.address}")
        return result
    }


    fun setSavedBluetoothPermissionGranted(granted: Boolean) {
        Log.d(GOKASA_PRINTER_DEBUG, "writing sharedPreference $SAVED_BLUETOOTH_PERMISSION_GRANTED $granted")
        SAVED_BLUETOOTH_PERMISSION_GRANTED.put(granted)
    }

    fun getSavedBluetoothPermissionGranted(): Boolean {
        val result = SAVED_BLUETOOTH_PERMISSION_GRANTED.getBoolean()
        Log.d(GOKASA_PRINTER_DEBUG, "reading sharedPreference $SAVED_BLUETOOTH_PERMISSION_GRANTED $result")
        return result
    }

    fun setSavedAppLanguage(language: String) {
        Log.d(GOKASA_PRINTER_DEBUG, "writing sharedPreference $SAVED_APP_LANGUAGE $language")
        SAVED_APP_LANGUAGE.put(language)
    }

    fun getSavedAppLanguage(): String {
        val result = SAVED_APP_LANGUAGE.getString()
        Log.d(GOKASA_PRINTER_DEBUG, "reading sharedPreference $SAVED_APP_LANGUAGE $result")
        return result
    }
}