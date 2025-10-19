package ru.krirll.moscowtour.backend.data

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.koin.core.annotation.Factory
import java.util.Collections

typealias SessionMap = MutableMap<Long, List<SessionCache.Writer>>

@Factory
class SessionCache {
    private val sessionMap: SessionMap = Collections.synchronizedMap(hashMapOf())

    private val mutex = Mutex()

    suspend fun put(userId: Long, socketSession: Writer) {
        eventLogger.info("put[$userId]=$socketSession")
        mutex.withLock {
            sessionMap[userId] = (sessionMap[userId] ?: mutableListOf()) + listOf(socketSession)
        }
    }

    suspend fun get(userId: Long): List<Writer>? {
        mutex.withLock {
            return sessionMap[userId]
        }
    }

    suspend fun remove(userId: Long, session: Writer) {
        eventLogger.info("remove[$userId]=$session")
        mutex.withLock {
            val sessionFromMap = sessionMap[userId]?.toMutableList()
            val item = sessionFromMap?.firstOrNull { it == session } ?: return
            sessionFromMap.remove(item)
            sessionMap[userId] = sessionFromMap
        }
    }

    fun interface Writer {
        suspend fun writeText(text: String)
    }
}
