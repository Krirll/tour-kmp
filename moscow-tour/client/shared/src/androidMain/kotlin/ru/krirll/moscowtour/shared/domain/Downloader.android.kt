package ru.krirll.moscowtour.shared.domain

import android.app.Application
import android.app.DownloadManager
import android.content.Context
import android.os.Environment
import androidx.core.net.toUri
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.koin.core.annotation.Factory

@Factory
actual class Downloader constructor(
    private val app: Application
) {

    private val _state = MutableStateFlow<DownloadState>(DownloadState.Idle)
    actual val state: Flow<DownloadState> = _state.asStateFlow()

    actual suspend fun download(url: String, filename: String) {
        try {
            _state.value = DownloadState.Wait

            val request = DownloadManager.Request(url.toUri())
                .setTitle(filename)
                .setDescription("Загрузка билета")
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, filename)

            val manager = app.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            val id = manager.enqueue(request)
            if (id == -1L) {
                throw CannotStartDownloadException("Не удалось начать загрузку", Throwable())
            }
            _state.value = DownloadState.Idle
        } catch (e: Exception) {
            _state.value = DownloadState.Error(e)
        }
    }
}

/* TODO: для айфона на будущее
* actual class Downloader actual constructor() : NSObject() {
    private val _state = MutableStateFlow<DownloadState>(DownloadState.Idle)
    actual val state = _state.asStateFlow()

    actual suspend fun download(url: String, filename: String) {
        try {
            _state.value = DownloadState.Wait

            val nsUrl = NSURL(string = url)
            val task = NSURLSession.sharedSession.downloadTaskWithURL(nsUrl) { location, response, error ->
                if (error != null) {
                    _state.value = DownloadState.Error(Exception(error.localizedDescription ?: "Unknown error"))
                    return@downloadTaskWithURL
                }

                if (location != null) {
                    val documents = NSFileManager.defaultManager.URLsForDirectory(
                        directory = NSDocumentDirectory,
                        inDomains = NSUserDomainMask
                    ).firstObject as NSURL

                    val dest = documents.URLByAppendingPathComponent(filename)
                    NSFileManager.defaultManager.moveItemAtURL(location, dest, null)
                }

                _state.value = DownloadState.Idle
            }
            task.resume()
        } catch (e: Exception) {
            _state.value = DownloadState.Error(e)
        }
    }
}*/
