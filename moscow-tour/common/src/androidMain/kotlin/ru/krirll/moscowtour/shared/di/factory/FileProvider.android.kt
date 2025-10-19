package ru.krirll.moscowtour.shared.di.factory

import android.content.Context
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import java.io.File

actual class FileProvider : KoinComponent {
    private val context: Context = get()
    actual fun getCacheFileDir(): String? = context.filesDir.absolutePath
}
