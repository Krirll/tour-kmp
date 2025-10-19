package ru.krirll.moscowtour.shared.data

import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import org.koin.core.component.KoinComponent
import ru.krirll.http.domain.TokenStorage
import ru.krirll.http.domain.hasAccount
import kotlin.coroutines.cancellation.CancellationException

private typealias WriteCallback<T> = suspend (List<T>) -> Unit

class RemoteSourceSynchronizer<T>(
    private val isSync: suspend () -> Boolean,
    private val setSync: suspend (Boolean) -> Unit,
    private val authTokenCache: TokenStorage,
    private val queryRemote: suspend (full: Boolean) -> List<T>,
    private val queryLocal: suspend (full: Boolean) -> List<T>,
    private val writeRemote: WriteCallback<T>,
    private val writeLocal: WriteCallback<T>,
    private val prepare: (T) -> T = { it }
) {
    val koin = object : KoinComponent { }

    suspend fun syncIfNeeded(query: Boolean): List<T> {
        val isSync = isSync()
        try {
            if (!authTokenCache.hasAccount()) {
                throw IllegalStateException("User account not found. Fallback to local cache")
            }
            if (!isSync) {
                sync()
                setSync(true)
            }
            if (query) {
                return queryRemote(false)
            }
        } catch (e: Exception) {
            if (e is CancellationException) {
                throw e
            }
            if (query) {
                if (authTokenCache.hasAccount() && isSync) {
                    setSync(false)
                }
                return queryLocal(false)
            }
        }
        return emptyList()
    }

    suspend fun synchronizeIfNeededAndQuery(): List<T> {
        return syncIfNeeded(true)
    }

    private suspend fun sync() {
        coroutineScope {
            val remoteDef = async { queryRemote(true) }
            val localDef = async { queryLocal(true) }
            val remoteList = remoteDef.await()
            val localList = localDef.await()
            val result = mutableSetOf<T>().apply {
                addAll(remoteList)
                addAll(localList)
            }
            val sync = mutableListOf<Deferred<*>>()
            sync.add(writeRemote.sync(result, remoteList))
            sync.add(writeLocal.sync(result, localList))
            sync.awaitAll()
        }
    }

    private suspend fun WriteCallback<T>.sync(
        result: Set<T>,
        current: List<T>
    ): Deferred<*> {
        val preparedCurrent = current.map { prepare(it) }
        return coroutineScope {
            async {
                val list = result.filterNot { prepare(it) in preparedCurrent }
                this@sync(list)
            }
        }
    }
}
