package cz.ethereal.gokasaprinter.components

import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cz.ethereal.gokasaprinter.GOKASA_PRINTER_DEBUG
import cz.ethereal.gokasaprinter.R
import cz.ethereal.gokasaprinter.data.PreferenceManager
import cz.ethereal.gokasaprinter.model.GlobalViewModel


@Composable
fun BluetoothStatusIndicator(
    model: GlobalViewModel,
    bluetoothPermissionLauncher: ActivityResultLauncher<Array<String>>,
    enableBluetoothLauncher: ActivityResultLauncher<Intent>,
) {
    Log.d(GOKASA_PRINTER_DEBUG, "BluetoothStatusIndicator rendering")
    val bluetoothStatus by model.bluetoothStatus.observeAsState()
    val bluetoothPermissionsGranted by model.bluetoothPermissionsGranted.observeAsState()
    val savedBluetoothPermissionGranted = PreferenceManager(LocalContext.current).getSavedBluetoothPermissionGranted()
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp),
        horizontalArrangement = Arrangement.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            bluetoothStatus?.let {
                IconWithText(
                    type = if (it != "enabled") "error" else "primary",
                    text = stringResource(
                        R.string.bluetooth,
                        if (it == "enabled") stringResource(R.string.enabled) else stringResource(R.string.disabled),
                    ),
                    fontSize = 12.sp,
                )
                if (it == "disabled") {
                    Button(onClick = { model.requestBluetoothPermissions(bluetoothPermissionLauncher, enableBluetoothLauncher) }) {
                        Text(stringResource(R.string.enable_bluetooth))
                    }
                }
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                bluetoothPermissionsGranted?.let {
                    IconWithText(
                        type = if (!it && !savedBluetoothPermissionGranted) "error" else "primary",
                        text = if (!it && !savedBluetoothPermissionGranted) {
                            stringResource(R.string.bluetooth_permissions_are_not_granted)
                        } else {
                            stringResource(R.string.bluetooth_permissions_are_granted)
                        },
                        fontSize = 12.sp,
                    )
                    if (!it && !savedBluetoothPermissionGranted) {
                        Button(onClick = { model.requestBluetoothPermissions(bluetoothPermissionLauncher, enableBluetoothLauncher) }) {
                            Text(stringResource(R.string.grant_bluetooth_permissions))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TextColor(type: String): Color {
    return when (type) {
        "error" -> MaterialTheme.colorScheme.error
        "primary" -> MaterialTheme.colorScheme.primary
        else -> MaterialTheme.colorScheme.secondary
    }
}

@Composable
fun TextIcon(type: String) {
    when (type) {
        "warning" -> Icon(
            Icons.Filled.Warning,
            contentDescription = null,
            tint = TextColor(type),
        )

        "primary" -> Icon(
            Icons.Filled.CheckCircle,
            contentDescription = null,
            tint = TextColor(type),
        )

        else -> Icon(
            Icons.Filled.Info,
            contentDescription = null,
            tint = TextColor(type),
        )
    }
}

@Composable
fun IconWithText(type: String, text: String, fontSize: TextUnit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        TextIcon(type)
        Spacer(Modifier.size(5.dp))
        Text(
            text = text,
            color = TextColor(type),
            fontSize = fontSize,
        )
    }
}
