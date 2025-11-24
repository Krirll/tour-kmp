package ru.krirll.moscowtour.shared.data

import android.app.Application
import android.net.Uri
import androidx.activity.result.ActivityResultCaller
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import kotlinx.coroutines.suspendCancellableCoroutine
import ru.krirll.moscowtour.shared.domain.model.TicketSavingException
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

object FileSaverAndroid {
    private var launcher: ActivityResultLauncher<String>? = null
    private var continuation: kotlinx.coroutines.CancellableContinuation<Uri>? = null
    private lateinit var context: Application

    fun register(activity: ActivityResultCaller, appContext: Application) {
        context = appContext
        launcher = activity.registerForActivityResult(
            ActivityResultContracts.CreateDocument("*/*")
        ) { uri ->
            continuation?.let { cont ->
                if (uri != null) cont.resume(uri)
                else cont.resumeWithException(TicketSavingException("Для продолжения необходимо сохранить файл"))
                continuation = null
            }
        }
    }

    suspend fun save(byteArray: ByteArray, fileName: String): Uri {
        val uri = suspendCancellableCoroutine { cont ->
            continuation = cont
            launcher?.launch(fileName) ?: throw IllegalStateException("Launcher not registered")
        }

        context.contentResolver.openOutputStream(uri)?.use {
            it.write(byteArray)
            it.flush()
        } ?: throw Exception("Unable to open output stream")

        return uri
    }
}

