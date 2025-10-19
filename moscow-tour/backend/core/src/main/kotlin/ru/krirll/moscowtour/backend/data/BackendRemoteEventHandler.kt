package ru.krirll.moscowtour.backend.data

import kotlinx.serialization.json.Json
import org.koin.core.annotation.Factory
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import ru.krirll.moscowtour.shared.domain.RemoteEvent
import ru.krirll.moscowtour.shared.domain.RemoteEventHandler

val eventLogger: Logger = LoggerFactory.getLogger("events")

@Factory
class BackendRemoteEventHandler(
    private val json: Json,
    private val sessionCache: SessionCache
) : RemoteEventHandler {

    suspend fun put(userId: Long, socketSession: SessionCache.Writer) {
        sessionCache.put(userId, socketSession)
    }

    suspend fun remove(userId: Long, socketSession: SessionCache.Writer) {
        sessionCache.remove(userId, socketSession)
    }

    override suspend fun notify(eventInfo: RemoteEvent) {
        val json = json.encodeToString(eventInfo)
        val writers = sessionCache.get(eventInfo.accountId)
        if (writers == null || writers.isEmpty()) {
            eventLogger.info("unable to notify $eventInfo. sessions not found, cache $sessionCache")
        } else {
            writers.forEach {
                eventLogger.info("notify[$eventInfo]. session $it", RuntimeException("called"))
                try {
                    it.writeText(json)
                } catch (_: Exception) {
                    eventLogger.error("unable to notify $it. delete session")
                    remove(eventInfo.accountId, it)
                }
            }
        }
    }
}
