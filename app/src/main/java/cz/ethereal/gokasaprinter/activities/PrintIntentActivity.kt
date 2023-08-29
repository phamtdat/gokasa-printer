package cz.ethereal.gokasaprinter.activities

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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cz.ethereal.gokasaprinter.GOKASA_PRINTER_DEBUG
import cz.ethereal.gokasaprinter.R
import cz.ethereal.gokasaprinter.model.GlobalViewModel
import cz.ethereal.gokasaprinter.ui.theme.GokasaPrinterTheme

class PrintIntentActivity : GokasaPrinterBaseActivity() {
    private val _context = this

    private val model: GlobalViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.d(GOKASA_PRINTER_DEBUG, "intent: ${intent.data.toString()}")
        if (intent.data != null) {
            val intentUri: Uri? = intent.data
            val printData: String = intentUri?.getQueryParameter("data").toString()
            Log.d(GOKASA_PRINTER_DEBUG, "Received intent data: ${printData.length} characters")
            model.updateBluetoothDevices(_context, false)
            model.print(
                _context,
                printData,
                onSendDataComplete = { finish() },
                onConnectFailed = { finish() },
            )
        }
        setContent {
            GokasaPrinterTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,
                ) {
                    PrintIntentScreen(model)
                }
            }
        }
    }
}

@Composable
fun PrintIntentScreen(model: GlobalViewModel) {
    val selectedBluetoothDevice by model.selectedBluetoothDevice.observeAsState()
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(text = stringResource(R.string.printing))
        selectedBluetoothDevice?.name?.let { Text(text = it) }
        selectedBluetoothDevice?.address?.let { Text(text = it, fontSize = 12.sp) }
        Spacer(modifier = Modifier.size(20.dp))
        CircularProgressIndicator()
    }
}