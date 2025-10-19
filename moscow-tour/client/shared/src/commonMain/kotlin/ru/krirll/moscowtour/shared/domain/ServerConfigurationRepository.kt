package ru.krirll.moscowtour.shared.domain

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

interface ServerConfigurationRepository {
    val serverConfiguration: Flow<ServerConfiguration>

    suspend fun setServerConfiguration(conf: ServerConfiguration)
}

suspend fun ServerConfigurationRepository.getServerConfiguration(): ServerConfiguration {
    return serverConfiguration.first()
}
