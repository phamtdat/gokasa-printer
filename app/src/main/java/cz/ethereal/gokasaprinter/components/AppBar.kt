package cz.ethereal.gokasaprinter.components

import android.app.Activity
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import cz.ethereal.gokasaprinter.R
import cz.ethereal.gokasaprinter.data.PreferenceManager
import cz.ethereal.gokasaprinter.ui.SupportedLanguages

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBar() {
    val context = LocalContext.current
    var isOpenedLanguageMenu by remember { mutableStateOf(false) }

    TopAppBar(
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Row {
                    Image(
                        painter = painterResource(
//                        if (isSystemInDarkTheme()) R.drawable.ic_logo_dark_theme else
                            R.drawable.ic_logo
                        ),
                        contentDescription = null,
                        modifier = Modifier.size(36.dp),
                    )
                    Spacer(modifier = Modifier.size(10.dp))
                    Text(
                        text = stringResource(R.string.app_name),
                    )
                }
                Row {
                    OutlinedIconButton(
                        onClick = { isOpenedLanguageMenu = true },
                        border = null,
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_translate),
                            contentDescription = null,
                            modifier = Modifier.size(20.dp),
                        )
                    }
                    DropdownMenu(
                        expanded = isOpenedLanguageMenu,
                        onDismissRequest = { isOpenedLanguageMenu = false },
                        modifier = Modifier.width(160.dp),
                    ) {
                        SupportedLanguages.languages.forEach { (code, label) ->
                            DropdownMenuItem(
                                trailingIcon = {
                                    Image(
                                        painter = painterResource(SupportedLanguages.flags[code]!!),
                                        contentDescription = code,
                                        modifier = Modifier.size(28.dp),
                                    )
                                },
                                text = { Text(label) },
                                onClick = {
                                    PreferenceManager(context).setSavedAppLanguage(code)
                                    (context as Activity).recreate()
                                },
                                modifier = Modifier.align(Alignment.End)
                            )
                        }
                    }
                }
            }
        },
    )
}
