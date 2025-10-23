package ru.krirll.moscowtour.shared.domain

import io.ktor.client.HttpClient
import org.koin.core.annotation.Factory
import ru.krirll.moscowtour.shared.data.get

@Factory
class DownloadTicketUseCase(
    private val httpClient: HttpClient,
    private val downloader: Downloader
) {
    suspend fun download(fileName: String) {
        httpClient.get<>()
    }
}
