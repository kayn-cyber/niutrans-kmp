package com.niutrans.app.storage

import com.niutrans.app.model.AppConfig
import java.util.prefs.Preferences

private class DesktopConfigStore : ConfigStore {
    private val prefs = Preferences.userRoot().node("com/niutrans/app")

    override fun load(): AppConfig = AppConfig(
        apiKey = prefs.get("apiKey", ""),
        fromLang = prefs.get("fromLang", "auto"),
        toLang = prefs.get("toLang", "zh"),
        isPinned = prefs.getBoolean("isPinned", true),
        isDarkTheme = prefs.getBoolean("isDarkTheme", false),
        opacity = prefs.getFloat("opacity", 0.96f),
        autoTranslate = prefs.getBoolean("autoTranslate", false),
    )

    override fun save(config: AppConfig) {
        prefs.put("apiKey", config.apiKey)
        prefs.put("fromLang", config.fromLang)
        prefs.put("toLang", config.toLang)
        prefs.putBoolean("isPinned", config.isPinned)
        prefs.putBoolean("isDarkTheme", config.isDarkTheme)
        prefs.putFloat("opacity", config.opacity)
        prefs.putBoolean("autoTranslate", config.autoTranslate)
    }
}

actual fun createConfigStore(): ConfigStore = DesktopConfigStore()
