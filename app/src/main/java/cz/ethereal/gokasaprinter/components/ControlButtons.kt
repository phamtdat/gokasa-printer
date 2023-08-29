package cz.ethereal.gokasaprinter.components

import android.content.Intent
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cz.ethereal.gokasaprinter.GOKASA_PRINTER_DEBUG
import cz.ethereal.gokasaprinter.R
import cz.ethereal.gokasaprinter.model.GlobalViewModel


@Composable
fun ControlButtons(
    model: GlobalViewModel,
    bluetoothPermissionLauncher: ActivityResultLauncher<Array<String>>,
    enableBluetoothLauncher: ActivityResultLauncher<Intent>,
) {
    Log.d(GOKASA_PRINTER_DEBUG, "ControlButtons rendering")
    val context = LocalContext.current
    val bluetoothStatus by model.bluetoothStatus.observeAsState()
    val bluetoothPermissionsGranted by model.bluetoothPermissionsGranted.observeAsState()
    val isBluetoothBusy by model.isBluetoothBusy.observeAsState()
    val enabled by remember { derivedStateOf { bluetoothStatus == "enabled" && bluetoothPermissionsGranted == true } }
    val selectedBluetoothDevice by model.selectedBluetoothDevice.observeAsState()

    if (isBluetoothBusy == true) {
        Text(text = stringResource(R.string.printing))
        selectedBluetoothDevice?.name?.let { Text(text = it) }
        selectedBluetoothDevice?.address?.let { Text(text = it, fontSize = 12.sp) }
        Spacer(modifier = Modifier.size(20.dp))
        CircularProgressIndicator()
    } else {
        Button(
            onClick = { model.printTestString(context) },
            modifier = Modifier.width(240.dp),
            enabled = enabled && selectedBluetoothDevice != null,
        ) {
            Text(text = stringResource(R.string.test_print))
        }
        Button(
            onClick = {
                if (bluetoothPermissionsGranted == true) {
                    model.updateBluetoothDevices(context, true)
                } else {
                    model.requestBluetoothPermissions(bluetoothPermissionLauncher, enableBluetoothLauncher)
                }
            },
            modifier = Modifier.width(240.dp),
            enabled = enabled,
        ) {
            Text(stringResource(R.string.refresh_paired_devices))
        }
    }
}
