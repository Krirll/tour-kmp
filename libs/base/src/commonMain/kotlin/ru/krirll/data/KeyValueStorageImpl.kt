package ru.krirll.data

import com.russhwolf.settings.ObservableSettings
import com.russhwolf.settings.Settings
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.krirll.domain.DispatcherProvider
import ru.krirll.domain.KeyValueStorage
import ru.krirll.domain.Log

private typealias NotifyCallback = ((String?) -> Unit)?

class SettingsKeyValueStorage(
    private val dispatcherProvider: DispatcherProvider,
    private val log: Log,
    private val name: String
) : KeyValueStorage {
    private val impl: BaseImpl by lazy {
        val settings = SettingsFactory().create(name)
        if (settings is ObservableSettings) {
            DefaultImpl(settings)
        } else {
            FallbackImpl(settings)
        }
    }

    override fun get(key: String): Flow<String?> {
        return impl.get(key)
    }

    override suspend fun remove(key: String) = withContext(dispatcherProvider.io) {
        impl.remove(key)
    }

    override suspend fun put(key: String, value: String) = withContext(dispatcherProvider.io){
        impl.put(key, value)
    }

    private abstract class BaseImpl(private val settings: Settings) {

        open fun remove(key: String) {
            settings.remove(key)
        }

        open fun put(key: String, value: String) {
            settings.putString(key, value)
        }

        abstract fun get(key: String): Flow<String?>
    }

    private inner class DefaultImpl(private val settings: ObservableSettings) : BaseImpl(settings) {
        override fun get(key: String): Flow<String?> {
            return callbackFlow {
                val listener: (String?) -> Unit = {
                    launch { send(it) }
                }
                send(settings.getStringOrNull(key))
                val disposable = settings.addStringOrNullListener(key, listener)
                awaitClose { disposable.deactivate() }
            }
        }
    }

    private inner class FallbackImpl(private val settings: Settings) : BaseImpl(settings) {
        private val listenerMap = hashMapOf<String, MutableList<NotifyCallback>>()

        override fun get(key: String): Flow<String?> = callbackFlow {
            val listener: (String?) -> Unit = {
                launch { send(it) }
            }
            send(settings.getStringOrNull(key))
            put(key, listener)
            awaitClose {
                remove(key, listener)
            }
        }

        private fun put(key: String, listener: NotifyCallback) {
            val list = listenerMap[key] ?: mutableListOf()
            list.add(listener)
            listenerMap[key] = list
        }

        private fun remove(key: String, listener: NotifyCallback) {
            val list = listenerMap[key] ?: return
            val index = list.indexOf(listener)
            if (index >= 0) {
                list.removeAt(index)
            }
            listenerMap[key] = list
        }

        override fun remove(key: String) {
            super.remove(key)
            listenerMap[key]?.forEach {
                it?.invoke(null)
            }
        }

        override fun put(key: String, value: String) {
            super.put(key, value)
            listenerMap[key]?.forEach {
                it?.invoke(value)
            }
        }
    }
}
