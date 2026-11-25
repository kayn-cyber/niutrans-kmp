package com.niutrans.app.storage

import com.niutrans.app.model.AppConfig

interface ConfigStore {
    fun load(): AppConfig
    fun save(config: AppConfig)
}

expect fun createConfigStore(): ConfigStore
