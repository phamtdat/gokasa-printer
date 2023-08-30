package cz.ethereal.gokasaprinter.activities

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cz.ethereal.gokasaprinter.GOKASA_PRINTER_DEBUG
import cz.ethereal.gokasaprinter.R
import cz.ethereal.gokasaprinter.data.BluetoothDeviceData
import cz.ethereal.gokasaprinter.data.PreferenceManager
import cz.ethereal.gokasaprinter.model.GlobalViewModel
import cz.ethereal.gokasaprinter.ui.theme.GokasaPrinterTheme

class PrintIntentActivity : GokasaPrinterBaseActivity() {
    private val _context = this

    private val model: GlobalViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val savedBluetoothDevice = PreferenceManager(_context).getSavedBluetoothDevice()
        Log.d(GOKASA_PRINTER_DEBUG, "savedBluetoothDevice: $savedBluetoothDevice")

        if (savedBluetoothDevice != null && intent.data != null) {
            val intentUri: Uri? = intent.data
            val printData: String = intentUri?.getQueryParameter("data").toString()
            Log.d(GOKASA_PRINTER_DEBUG, "Received printData: ${printData.length} characters")
            model.updateBluetoothDevices(_context, false)
            model.print(
                _context,
                printData,
                onSendDataComplete = { finishAndRemoveTask() },
                onConnectFailed = { finishAndRemoveTask() },
            )
        }
        setContent {
            GokasaPrinterTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,
                ) {
                    PrintIntentScreen(savedBluetoothDevice)
                }
            }
        }
    }
}

@Composable
fun PrintIntentScreen(selectedBluetoothDevice: BluetoothDeviceData?) {
    val context = LocalContext.current
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        if (selectedBluetoothDevice == null) {
            Text(stringResource(R.string.select_your_printer))
            Spacer(Modifier.size(20.dp))
            Button(
                onClick = {
                    context.startActivity(Intent(context, MainActivity::class.java))
                    (context as Activity).finish()
                },
            ) {
                Text(stringResource(R.string.go_to_app_settings))
                Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                Icon(Icons.Default.Settings, contentDescription = null)
            }
        } else {
            Text(stringResource(R.string.printing))
            selectedBluetoothDevice.name?.let { Text(text = it) }
            Text(selectedBluetoothDevice.address, fontSize = 12.sp)
            Spacer(Modifier.size(20.dp))
            CircularProgressIndicator()
        }
    }
}