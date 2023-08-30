package cz.ethereal.gokasaprinter.activities

import android.content.Context
import android.content.ContextWrapper
import android.util.Log
import androidx.activity.ComponentActivity
import cz.ethereal.gokasaprinter.GOKASA_PRINTER_DEBUG
import cz.ethereal.gokasaprinter.data.PreferenceManager
import cz.ethereal.gokasaprinter.ui.AppLanguage
import cz.ethereal.gokasaprinter.ui.ContextUtils
import java.util.Locale

open class GokasaPrinterBaseActivity : ComponentActivity() {

    override fun attachBaseContext(newBase: Context) {
        val preferenceManager = PreferenceManager(newBase)
        val savedAppLanguage = preferenceManager.getSavedAppLanguage()
        Log.d(GOKASA_PRINTER_DEBUG, "savedAppLanguage: $savedAppLanguage")
        val language = savedAppLanguage.ifEmpty {
            Log.d(GOKASA_PRINTER_DEBUG, "checking systemLanguage: ${AppLanguage.systemLanguage}")
            if (!AppLanguage.supportedLanguages.containsKey(AppLanguage.systemLanguage)) {
                Log.d(GOKASA_PRINTER_DEBUG, "systemLanguage is not supported, using default app language: ${AppLanguage.defaultAppLanguage}")
                AppLanguage.defaultAppLanguage
            } else {
                Log.d(GOKASA_PRINTER_DEBUG, "systemLanguage is supported: ${AppLanguage.systemLanguage}")
                AppLanguage.systemLanguage
            }
        }
        Log.d(GOKASA_PRINTER_DEBUG, "language to use: $language")
        if (language != savedAppLanguage) {
            preferenceManager.setSavedAppLanguage(language)
        }
        val newLocale = Locale(language)
        val localeUpdatedContext: ContextWrapper = ContextUtils.updateLocale(newBase, newLocale)
        super.attachBaseContext(localeUpdatedContext)
    }

}