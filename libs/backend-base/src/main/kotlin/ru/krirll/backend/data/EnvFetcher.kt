package ru.krirll.backend.data

import org.koin.core.annotation.Single

@Single
class EnvFetcher {
    fun get(key: String, default: String = ""): String {
        val value = System.getenv(key)
        return value?.ifEmpty { default } ?: default
    }
}
