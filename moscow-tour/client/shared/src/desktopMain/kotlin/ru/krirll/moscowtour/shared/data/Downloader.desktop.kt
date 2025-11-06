package ru.krirll.moscowtour.shared.data

import io.ktor.utils.io.ByteReadChannel

actual suspend fun saveFileFromResponse(byteChannel: ByteReadChannel, fileName: String) {}
