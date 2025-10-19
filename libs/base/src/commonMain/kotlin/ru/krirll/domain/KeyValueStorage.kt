package ru.krirll.domain

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

interface KeyValueStorage {
    fun get(key: String): Flow<String?>
    fun getBoolean(key: String): Flow<Boolean?> = get(key).map { it?.toBoolean() }
    fun getInt(key: String): Flow<Int?> = get(key).map { it?.toInt() }

    suspend fun remove(key: String)
    suspend fun put(key: String, value: String)
    suspend fun putBoolean(key: String, value: Boolean) = put(key, value.toString())
    suspend fun putInt(key: String, value: Int) = put(key, value.toString())
}
