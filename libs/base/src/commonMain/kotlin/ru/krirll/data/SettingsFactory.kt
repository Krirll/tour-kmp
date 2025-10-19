package ru.krirll.data

import com.russhwolf.settings.Settings

expect class SettingsFactory() {
    fun create(name: String): Settings
}
