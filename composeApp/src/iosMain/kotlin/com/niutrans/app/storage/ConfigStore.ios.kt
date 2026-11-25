package com.niutrans.app.storage

import com.niutrans.app.model.AppConfig
import platform.Foundation.NSUserDefaults

private class IosConfigStore : ConfigStore {
    private val defaults = NSUserDefaults.standardUserDefaults

    override fun load(): AppConfig = AppConfig(
        apiKey = defaults.stringForKey("niutrans_apiKey") ?: "",
        fromLang = defaults.stringForKey("niutrans_fromLang") ?: "auto",
        toLang = defaults.stringForKey("niutrans_toLang") ?: "zh",
        isPinned = true,
        isDarkTheme = defaults.boolForKey("niutrans_isDarkTheme"),
        opacity = 1.0f,
        autoTranslate = defaults.boolForKey("niutrans_autoTranslate"),
    )

    override fun save(config: AppConfig) {
        defaults.setObject(config.apiKey, forKey = "niutrans_apiKey")
        defaults.setObject(config.fromLang, forKey = "niutrans_fromLang")
        defaults.setObject(config.toLang, forKey = "niutrans_toLang")
        defaults.setBool(config.isDarkTheme, forKey = "niutrans_isDarkTheme")
        defaults.setBool(config.autoTranslate, forKey = "niutrans_autoTranslate")
    }
}

actual fun createConfigStore(): ConfigStore = IosConfigStore()
