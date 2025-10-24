package ru.krirll.moscowtour.shared.data

import android.app.Application
import android.content.ContentValues
import android.provider.MediaStore
import kotlinx.coroutines.withContext
import okio.buffer
import okio.sink
import ru.krirll.koin
import ru.krirll.moscowtour.shared.di.factory.DispatcherProvider

actual class PlatformDownloader actual constructor(
    private val downloader: Downloader,
    private val dispatcherProvider: DispatcherProvider
) {
    actual suspend fun downloadFile(url: String) {
        val appContext = koin.get<Application>()
        val resolver = appContext.contentResolver
        val values = ContentValues().apply {
            put(MediaStore.Downloads.DISPLAY_NAME, "fileName")
            put(MediaStore.Downloads.MIME_TYPE, "")
            put(MediaStore.Downloads.IS_PENDING, 1)
        }

        withContext(dispatcherProvider.io) {
            val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values)
                ?: throw IllegalStateException("Cannot insert into MediaStore")
            val out = resolver.openOutputStream(uri)
                ?: throw IllegalStateException("Cannot open OutputStream")

            out.use { stream ->
                val sink = stream.sink().buffer()
                downloader.downloadToSink(url, sink)
            }

            values.clear()
            values.put(MediaStore.Downloads.IS_PENDING, 0)
            resolver.update(uri, values, null, null)
        }
    }
}
