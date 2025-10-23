package ru.krirll.moscowtour.shared.domain

import kotlinx.browser.window
import kotlinx.coroutines.flow.emptyFlow
import org.koin.core.annotation.Factory

@Factory
actual class Downloader {

    actual val state = emptyFlow<DownloadState>()

    actual suspend fun download(url: String, filename: String) {
        window.location.href = url
    }
}
