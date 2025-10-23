package ru.krirll.moscowtour.shared.domain

import kotlinx.coroutines.flow.Flow
import org.koin.core.annotation.Factory

@Factory
expect class Downloader {
    val state: Flow<DownloadState>
    suspend fun download(url: String, filename: String)
}

sealed interface DownloadState {
    data object Idle : DownloadState
    data object Wait : DownloadState
    data class Downloading(val progress: Int) : DownloadState
    data class Error(val e: Throwable) : DownloadState
}

open class DownloadException(
    override val message: String?
) : IllegalStateException(message)

class CannotStartDownloadException(
    override val message: String?
) : DownloadException(message)


