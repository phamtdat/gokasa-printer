package cz.ethereal.gokasaprinter.activities

import android.content.Context
import android.content.ContextWrapper
import androidx.activity.ComponentActivity
import cz.ethereal.gokasaprinter.data.PreferenceManager
import cz.ethereal.gokasaprinter.ui.ContextUtils
import java.util.Locale

open class GokasaPrinterBaseActivity : ComponentActivity() {

    override fun attachBaseContext(newBase: Context) {
        val newLocale = Locale(PreferenceManager(newBase).getSavedAppLanguage())
        val localeUpdatedContext: ContextWrapper = ContextUtils.updateLocale(newBase, newLocale)
        super.attachBaseContext(localeUpdatedContext)
    }

}