package cz.ethereal.gokasaprinter.ui

import android.content.res.Resources
import cz.ethereal.gokasaprinter.R

object AppLanguage {

    const val defaultAppLanguage = "en"

    val supportedLanguages = mapOf(
        "en" to "English",
        "cs" to "Čeština",
    )

    val countryFlags = mapOf(
        "en" to R.drawable.flag_en,
        "cs" to R.drawable.flag_cs,
    )

    val systemLanguage: String = Resources.getSystem().configuration.locales[0].language
}
