package cz.ethereal.gokasaprinter.components

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.sp
import cz.ethereal.gokasaprinter.GOKASA_PRINTER_DEBUG
import cz.ethereal.gokasaprinter.R
import cz.ethereal.gokasaprinter.model.GlobalViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BluetoothDevicesDropdownMenu(model: GlobalViewModel) {
    Log.d(GOKASA_PRINTER_DEBUG, "BluetoothDevicesDropdownMenu rendering")
    val context = LocalContext.current
    var expanded by remember { mutableStateOf(false) }
    val bluetoothDevices by model.bluetoothDevices.observeAsState()
    val selectedBluetoothDevice by model.selectedBluetoothDevice.observeAsState()
    val bluetoothStatus by model.bluetoothStatus.observeAsState()
    val bluetoothPermissionsGranted by model.bluetoothPermissionsGranted.observeAsState()
    val isBluetoothConnecting by model.isBluetoothBusy.observeAsState()
    val enabled by remember { derivedStateOf { bluetoothStatus == "enabled" && bluetoothPermissionsGranted == true && isBluetoothConnecting == false } }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = {
            if (enabled) {
                expanded = !expanded
            }
        },
    ) {
        TextField(
            modifier = Modifier.menuAnchor(),
            readOnly = true,
            value = selectedBluetoothDevice?.name ?: stringResource(R.string.default_selected_device_name),
            onValueChange = {},
            label = { Text(stringResource(R.string.select_your_printer)) },
            trailingIcon = { if (enabled) ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            colors = ExposedDropdownMenuDefaults.textFieldColors(),
            enabled = enabled,
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            bluetoothDevices?.forEach { bluetoothDevice ->
                DropdownMenuItem(
                    text = {
                        Column {
                            Text(bluetoothDevice.name ?: stringResource(R.string.unknown_bluetooth_device_name))
                            Text(bluetoothDevice.address, fontSize = 10.sp)
                        }
                    },
                    onClick = {
                        expanded = false
                        model.setSelectedBluetoothDevice(bluetoothDevice, context)
                    },
                    enabled = selectedBluetoothDevice?.address != bluetoothDevice.address,
                )
            }
        }
    }
}
