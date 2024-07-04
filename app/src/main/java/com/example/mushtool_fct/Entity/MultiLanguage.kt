package com.example.mushtool_fct.Entity

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import java.util.Locale


class MultiLanguage {

    fun cambiarIdioma(idioma: String, context: Context) {
        IdiomaManager.actualizarIdioma(idioma, context)
        recreateActivity(context)
    }

    fun recreateActivity(context: Context) {
        val intent = context.packageManager.getLaunchIntentForPackage(context.packageName)
        intent?.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        context.startActivity(intent)
    }

    object IdiomaManager {
        private const val PREFERENCIAS_IDIOMA = "idioma_pref"
        private const val IDIOMA_KEY = "idioma"

        fun actualizarIdioma(idioma: String, context: Context) {
            val locale = Locale(idioma)
            Locale.setDefault(locale)

            val resources = context.resources
            val configuration = Configuration(resources.configuration)
            configuration.setLocale(locale)

            context.createConfigurationContext(configuration)
            resources.updateConfiguration(configuration, resources.displayMetrics)

            // Guardar el idioma seleccionado en SharedPreferences
            guardarIdiomaSeleccionado(idioma, context)
        }

        fun cargarIdiomaGuardado(context: Context): String {
            val sharedPreferences =
                context.getSharedPreferences(PREFERENCIAS_IDIOMA, Context.MODE_PRIVATE)
            return sharedPreferences.getString(IDIOMA_KEY, Locale.getDefault().language)
                ?: Locale.getDefault().language
        }

        private fun guardarIdiomaSeleccionado(idioma: String, context: Context) {
            val sharedPreferences =
                context.getSharedPreferences(PREFERENCIAS_IDIOMA, Context.MODE_PRIVATE)
            sharedPreferences.edit().putString(IDIOMA_KEY, idioma).apply()
        }
    }

}