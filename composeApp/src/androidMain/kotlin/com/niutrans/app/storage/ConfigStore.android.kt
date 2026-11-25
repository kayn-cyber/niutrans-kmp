package com.niutrans.app.storage

import android.content.Context
import com.niutrans.app.model.AppConfig

object AndroidContextHolder {
    lateinit var appContext: Context
}

private class AndroidConfigStore(context: Context) : ConfigStore {
    private val prefs = context.getSharedPreferences("niutrans_prefs", Context.MODE_PRIVATE)

    override fun load(): AppConfig = AppConfig(
        apiKey = prefs.getString("apiKey", "") ?: "",
        fromLang = prefs.getString("fromLang", "auto") ?: "auto",
        toLang = prefs.getString("toLang", "zh") ?: "zh",
        isPinned = true,
        isDarkTheme = prefs.getBoolean("isDarkTheme", false),
        opacity = 1.0f,
        autoTranslate = prefs.getBoolean("autoTranslate", false),
    )

    override fun save(config: AppConfig) {
        prefs.edit()
            .putString("apiKey", config.apiKey)
            .putString("fromLang", config.fromLang)
            .putString("toLang", config.toLang)
            .putBoolean("isDarkTheme", config.isDarkTheme)
            .putBoolean("autoTranslate", config.autoTranslate)
            .apply()
    }
}

actual fun createConfigStore(): ConfigStore = AndroidConfigStore(AndroidContextHolder.appContext)
