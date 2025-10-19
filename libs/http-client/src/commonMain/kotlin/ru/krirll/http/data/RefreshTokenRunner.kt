package ru.krirll.http.data

import io.ktor.client.plugins.auth.providers.BearerTokens
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.koin.core.annotation.Factory
import ru.krirll.domain.DispatcherProvider

@Factory
class RefreshTokenRunner(
    private val dispatcherProvider: DispatcherProvider
) {
    private val scope = CoroutineScope(SupervisorJob() + dispatcherProvider.io)
    private val lock = Mutex()
    private var inFlight: Deferred<BearerTokens?>? = null

    suspend fun runOrJoin(block: suspend () -> BearerTokens?): BearerTokens? {
        val deferred = lock.withLock {
            inFlight?.takeIf { it.isActive } ?: startNew(block).also { inFlight = it }
        }
        return deferred.await()
    }

    private fun startNew(block: suspend () -> BearerTokens?): Deferred<BearerTokens?> {
        val d = scope.async(dispatcherProvider.io) { block() }
        d.invokeOnCompletion {
            scope.launch {
                lock.withLock {
                    if (inFlight === d) inFlight = null
                }
            }
        }
        return d
    }
}
