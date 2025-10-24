package ru.krirll.moscowtour.shared.data

import kotlinx.browser.window
import ru.krirll.moscowtour.shared.di.factory.DispatcherProvider

actual class PlatformDownloader actual constructor(
    downloader: Downloader,
    dispatcherProvider: DispatcherProvider
) {
    actual suspend fun downloadFile(url: String) {
        window.location.href = url
        //todo нужно открыть ссылку на новой вкладке, как только начнется загрузка - закрыть
    }
}
