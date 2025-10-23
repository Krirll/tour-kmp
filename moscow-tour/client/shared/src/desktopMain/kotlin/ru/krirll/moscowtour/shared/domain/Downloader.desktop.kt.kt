package ru.krirll.moscowtour.shared.domain

import kotlinx.coroutines.flow.Flow
import org.koin.core.annotation.Factory

@Factory
actual class Downloader {
    
    actual val state: Flow<DownloadState>
        get() = TODO("Not yet implemented")

    actual suspend fun download(url: String, filename: String) {
    }
}
