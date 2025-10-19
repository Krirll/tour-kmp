package ru.krirll.domain

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

interface DispatcherProvider {
    val io: CoroutineDispatcher get() = Dispatchers.Default
    val main: CoroutineDispatcher get() = Dispatchers.Main
    val unconfined: CoroutineDispatcher get() = Dispatchers.Unconfined
    val default: CoroutineDispatcher get() = Dispatchers.Default
}
