package ru.krirll.moscowtour.shared.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.koin.core.annotation.Factory
import ru.krirll.domain.KeyValueStorage
import ru.krirll.moscowtour.shared.domain.ServerConfiguration
import ru.krirll.moscowtour.shared.domain.ServerConfigurationRepository

@Factory
class DefaultServerConfigurationRepository(
    private val storage: KeyValueStorage
) : ServerConfigurationRepository {
    override val serverConfiguration: Flow<ServerConfiguration> = storage.get(KEY)
        .map {
            if (it != null) {
                ServerConfiguration(it)
            } else {
                ServerConfiguration()
            }
        }

    override suspend fun setServerConfiguration(conf: ServerConfiguration) {
        storage.put(KEY, conf.asHttpStr())
    }

    private companion object {
        const val KEY = "custom_server_uri"
    }
}
