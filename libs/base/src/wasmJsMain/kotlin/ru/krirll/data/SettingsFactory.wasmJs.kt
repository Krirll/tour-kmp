package ru.krirll.data

import com.russhwolf.settings.Settings
import com.russhwolf.settings.StorageSettings

actual class SettingsFactory actual constructor() {
    actual fun create(name: String): Settings {
        return StorageSettings()
    }
}
