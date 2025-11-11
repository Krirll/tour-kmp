package ru.krirll.moscowtour.shared.data

import android.app.Application
import android.content.Context
import android.net.Uri
import androidx.activity.result.ActivityResultCaller
import androidx.activity.result.contract.ActivityResultContracts
import kotlinx.coroutines.suspendCancellableCoroutine
import ru.krirll.moscowtour.shared.di.koin
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

actual suspend fun saveFileFromResponse(byteArray: ByteArray, fileName: String) {
    val context: Context = koin.get<Application>()
    val resultUri: Uri = suspendCancellableCoroutine { cont ->
        val launcher = (context as ActivityResultCaller).registerForActivityResult(
            ActivityResultContracts.CreateDocument(fileName)
        ) { uri ->
            if (uri != null) cont.resume(uri)
            else cont.resumeWithException(Exception("File save canceled"))
        }
        launcher.launch(fileName)
    }
    context.contentResolver.openOutputStream(resultUri)?.use { output ->
        output.write(byteArray)
        output.flush()
    } ?: throw Exception("Unable to open output stream for URI: $resultUri")

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
