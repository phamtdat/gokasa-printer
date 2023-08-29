package cz.ethereal.gokasaprinter.activities

import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import cz.ethereal.gokasaprinter.GOKASA_PRINTER_DEBUG
import cz.ethereal.gokasaprinter.bluetooth.BluetoothController
import cz.ethereal.gokasaprinter.components.MainScreen
import cz.ethereal.gokasaprinter.data.PreferenceManager
import cz.ethereal.gokasaprinter.model.GlobalViewModel
import cz.ethereal.gokasaprinter.ui.theme.GokasaPrinterTheme

class MainActivity : GokasaPrinterBaseActivity() {
    private val _context = this

    private val model: GlobalViewModel by viewModels()

    private val bluetoothManager by lazy {
        applicationContext.getSystemService(BluetoothManager::class.java)
    }
    private val bluetoothAdapter by lazy {
        bluetoothManager?.adapter
    }
    private val isBluetoothEnabled: Boolean
        get() = bluetoothAdapter?.isEnabled == true

    private val enableBluetoothLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { enableBluetoothLaunchResult ->
        Log.d(GOKASA_PRINTER_DEBUG, "enableBluetoothLaunchResult $enableBluetoothLaunchResult")
        if (enableBluetoothLaunchResult.resultCode == Activity.RESULT_OK) {
            model.updateBluetoothDevices(_context, true)
        }
    }

    private val bluetoothPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissionLaunchResults ->
        Log.d(GOKASA_PRINTER_DEBUG, permissionLaunchResults.toString())
        val canEnableBluetooth = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            permissionLaunchResults[Manifest.permission.BLUETOOTH_CONNECT] == true
        } else true

        if (canEnableBluetooth && !isBluetoothEnabled) {
            enableBluetoothLauncher.launch(
                Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE),
            )
        }

        if (permissionLaunchResults[Manifest.permission.BLUETOOTH_SCAN] == true) {
            PreferenceManager(_context).setSavedBluetoothPermissionGranted(true)
            model.setBluetoothPermissionsGranted(true)
            model.updateBluetoothDevices(_context, true)
        }
    }

    private val broadCastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent?) {
            val action = intent?.action
            Log.d(GOKASA_PRINTER_DEBUG, "broadCastReceiver ${action.toString()}")
            if (BluetoothAdapter.ACTION_STATE_CHANGED == action) {
                val bluetoothStatus = BluetoothController(context).getBluetoothStatus()
                Log.d(GOKASA_PRINTER_DEBUG, "bluetoothStatus $bluetoothStatus")
                model.setBluetoothStatus(bluetoothStatus)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        registerReceiver(broadCastReceiver, IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED))
        model.setBluetoothStatus(BluetoothController(_context).getBluetoothStatus())
        model.requestBluetoothPermissions(bluetoothPermissionLauncher, enableBluetoothLauncher)

        setContent {
            GokasaPrinterTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,
                ) {
                    MainScreen(model, bluetoothPermissionLauncher, enableBluetoothLauncher)
                }
            }
        }
    }
}
