package ru.krirll.data

import com.russhwolf.settings.PreferencesSettings
import com.russhwolf.settings.Settings
import java.util.prefs.Preferences

actual class SettingsFactory actual constructor() {
    actual fun create(name: String): Settings {
        return PreferencesSettings(Preferences.userRoot().node(name))
    }
}
