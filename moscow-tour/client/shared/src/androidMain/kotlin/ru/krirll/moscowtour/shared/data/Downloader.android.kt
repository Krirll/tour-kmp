package ru.krirll.moscowtour.shared.data

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.result.ActivityResultCaller
import androidx.activity.result.contract.ActivityResultContracts
import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.readAvailable
import kotlinx.coroutines.suspendCancellableCoroutine
import ru.krirll.moscowtour.shared.di.koin
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

actual suspend fun saveFileFromResponse(byteChannel: ByteReadChannel, fileName: String) {
    val context: Context = koin.get() // твой способ получить Context

    val resultUri: Uri = suspendCancellableCoroutine { cont ->
        val launcher = (context as ActivityResultCaller).registerForActivityResult(
            ActivityResultContracts.CreateDocument(fileName)
        ) { uri ->
            if (uri != null) cont.resume(uri)
            else cont.resumeWithException(Exception("File save canceled"))
        }
        launcher.launch(Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
            putExtra(Intent.EXTRA_TITLE, fileName)
        })
    }

    context.contentResolver.openOutputStream(resultUri).use { output ->
        output?.let {
            val buffer = ByteArray(4096)
            while (!byteChannel.isClosedForRead) {
                val read = byteChannel.readAvailable(buffer)
                if (read > 0) output.write(buffer, 0, read)
            }
        }
    }

    //todo для ios
    /**
    val picker = UIDocumentPickerViewController(forExporting = listOf(NSURL.fileURLWithPath(fileName)))
    picker.delegate = object : NSObject(), UIDocumentPickerDelegateProtocol {
    override fun documentPicker(controller: UIDocumentPickerViewController, didPickDocumentsAtURLs: List<NSURL>) {
    val url = didPickDocumentsAtURLs.firstOrNull()
    if (url != null) cont.resume(url.absoluteString)
    else cont.resumeWithException(Exception("File save canceled"))
    }
    }
     */
}
