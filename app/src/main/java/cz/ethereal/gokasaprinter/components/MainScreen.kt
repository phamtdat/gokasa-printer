package cz.ethereal.gokasaprinter.components

import android.content.Intent
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import cz.ethereal.gokasaprinter.GOKASA_PRINTER_DEBUG
import cz.ethereal.gokasaprinter.model.GlobalViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    model: GlobalViewModel,
    bluetoothPermissionLauncher: ActivityResultLauncher<Array<String>>,
    enableBluetoothLauncher: ActivityResultLauncher<Intent>,
) {
    Log.d(GOKASA_PRINTER_DEBUG, "MainScreen rendering")
    Scaffold(
        topBar = { AppBar() },
        content = { innerPadding ->
            MainScreenContent(model, bluetoothPermissionLauncher, enableBluetoothLauncher, innerPadding)
            BluetoothRequiredDialog(model)
            BluetoothConnectionFailedDialog(model)
        }
    )
}

