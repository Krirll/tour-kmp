package ru.krirll.moscowtour.shared.data

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.statement.bodyAsChannel
import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.readAvailable
import kotlinx.coroutines.withContext
import okio.Buffer
import okio.BufferedSink
import org.koin.core.annotation.Factory
import ru.krirll.moscowtour.shared.di.factory.DispatcherProvider
import ru.krirll.moscowtour.shared.domain.ServerConfigurationRepository
import ru.krirll.moscowtour.shared.domain.TicketsRepository
import ru.krirll.moscowtour.shared.domain.getServerConfiguration

expect class PlatformDownloader(
    downloader: Downloader,
    dispatcherProvider: DispatcherProvider
) {
    suspend fun downloadFile(url: String)
}

@Factory
class Downloader(
    private val httpClient: HttpClient,
    private val serverConfigurationRepository: ServerConfigurationRepository,
    private val dispatcherProvider: DispatcherProvider
) {

    suspend fun downloadToSink(
        filePath: String,
        sink: BufferedSink
    ) {
        try {
            val rsp = httpClient.get {
                serverConfigurationRepository
                    .getServerConfiguration()
                    .apply(this, TicketsRepository.DOWNLOAD)
                parameter(TicketsRepository.FILE_NAME_ARG, filePath)
            }
            val channel: ByteReadChannel = rsp.bodyAsChannel()
            val buffer = Buffer()
            val tmp = ByteArray(8 * 1024)
            var readBytes = 0L

            withContext(dispatcherProvider.io) {
                while (!channel.isClosedForRead) {
                    val bytes = channel.readAvailable(tmp, 0, tmp.size)
                    if (bytes <= 0) break
                    buffer.write(tmp, 0, bytes)
                    sink.write(buffer, buffer.size)
                    sink.emit()
                    readBytes += bytes
                }
                sink.flush()
            }
        }  finally {
            withContext(dispatcherProvider.io) {
                sink.close()
            }
        }
    }
}
