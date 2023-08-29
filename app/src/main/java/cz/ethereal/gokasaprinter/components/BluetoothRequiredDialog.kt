package cz.ethereal.gokasaprinter.components

import android.util.Log
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import cz.ethereal.gokasaprinter.GOKASA_PRINTER_DEBUG
import cz.ethereal.gokasaprinter.R
import cz.ethereal.gokasaprinter.data.PreferenceManager
import cz.ethereal.gokasaprinter.model.GlobalViewModel


@Composable
fun BluetoothRequiredDialog(model: GlobalViewModel) {
    Log.d(GOKASA_PRINTER_DEBUG, "BluetoothRequiredDialog rendering")
    val bluetoothStatus by model.bluetoothStatus.observeAsState()
    val bluetoothPermissionsGranted by model.bluetoothPermissionsGranted.observeAsState()
    val savedBluetoothPermissionGranted = PreferenceManager(LocalContext.current).getSavedBluetoothPermissionGranted()
    if (bluetoothStatus == "enabled" && (bluetoothPermissionsGranted == true || savedBluetoothPermissionGranted)) {
        return
    }
    var isDialogOpen by remember { mutableStateOf(true) }
    if (!isDialogOpen) {
        return
    }
    AlertDialog(
        onDismissRequest = { isDialogOpen = false },
        title = { Text(stringResource(R.string.bluetooth_permission)) },
        text = {
            Text(
                listOf(
                    stringResource(R.string.bluetooth_is_required_to_use_this_app),
                    stringResource(R.string.please_turn_on_bluetooth_and_grant_the_permission_to_continue),
                ).joinToString(separator = System.lineSeparator().repeat(2))
            )
        },
        confirmButton = {
            TextButton(onClick = { isDialogOpen = false }) {
                Text(stringResource(R.string.ok))
            }
        },
    )
}
