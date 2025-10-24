package ru.krirll.moscowtour.shared.data

import ru.krirll.moscowtour.shared.di.factory.DispatcherProvider

actual class PlatformDownloader actual constructor(
    downloader: Downloader,
    dispatcherProvider: DispatcherProvider
) {
    actual suspend fun downloadFile(url: String) {
        //nothing
    }
}
