package cz.ethereal.gokasaprinter.components

import android.util.Log
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.res.stringResource
import cz.ethereal.gokasaprinter.GOKASA_PRINTER_DEBUG
import cz.ethereal.gokasaprinter.R
import cz.ethereal.gokasaprinter.model.GlobalViewModel


@Composable
fun BluetoothConnectionFailedDialog(model: GlobalViewModel) {
    Log.d(GOKASA_PRINTER_DEBUG, "BluetoothConnectionFailedDialog rendering")
    val isBluetoothConnectFailed by model.isBluetoothConnectFailed.observeAsState()
    if (isBluetoothConnectFailed == false) {
        return
    }
    AlertDialog(
        onDismissRequest = { model.setIsBluetoothConnectFailed(false) },
        title = { Text(stringResource(R.string.connection_failed)) },
        text = { Text(stringResource(R.string.failed_to_connect_to_bluetooth_device)) },
        confirmButton = {
            TextButton(onClick = { model.setIsBluetoothConnectFailed(false) }) {
                Text(stringResource(R.string.ok))
            }
        },
    )
}
