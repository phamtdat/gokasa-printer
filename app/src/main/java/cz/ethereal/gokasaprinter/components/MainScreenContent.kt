package cz.ethereal.gokasaprinter.components

import android.content.Intent
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cz.ethereal.gokasaprinter.GOKASA_PRINTER_DEBUG
import cz.ethereal.gokasaprinter.model.GlobalViewModel

@Composable
fun MainScreenContent(
    model: GlobalViewModel,
    bluetoothPermissionLauncher: ActivityResultLauncher<Array<String>>,
    enableBluetoothLauncher: ActivityResultLauncher<Intent>,
    innerPadding: PaddingValues,
) {
    Log.d(GOKASA_PRINTER_DEBUG, "MainScreenContent rendering")
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(innerPadding),
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(Modifier.size(48.dp))
            BluetoothDevicesDropdownMenu(model)
            Spacer(Modifier.size(48.dp))
            ControlButtons(model, bluetoothPermissionLauncher, enableBluetoothLauncher)
        }
        Column {
            BluetoothStatusIndicator(model, bluetoothPermissionLauncher, enableBluetoothLauncher)
            Credits()
        }
    }
}
