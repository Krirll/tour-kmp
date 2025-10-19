package ru.krirll.data

import android.app.Application
import android.content.Context
import com.russhwolf.settings.Settings
import com.russhwolf.settings.SharedPreferencesSettings
import org.koin.core.component.get
import ru.krirll.koin

actual class SettingsFactory actual constructor() {
    actual fun create(name: String): Settings {
        val prefs = koin.get<Application>().getSharedPreferences(name, Context.MODE_PRIVATE)
        return SharedPreferencesSettings(prefs)
    }
}
