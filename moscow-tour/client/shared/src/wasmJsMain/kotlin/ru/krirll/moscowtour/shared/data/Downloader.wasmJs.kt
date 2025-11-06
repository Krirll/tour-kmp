package ru.krirll.moscowtour.shared.data

import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.readAvailable
import io.ktor.utils.io.readFully
import kotlinx.browser.document
import org.khronos.webgl.Uint8Array
import org.w3c.dom.HTMLAnchorElement
import org.w3c.files.Blob
import org.w3c.files.BlobPropertyBag
import kotlin.collections.forEachIndexed

actual suspend fun saveFileFromResponse(byteChannel: ByteReadChannel, fileName: String) {
    val byteList = mutableListOf<Byte>()
    while (!byteChannel.isClosedForRead) {
        val read = byteChannel.readAvailable(ByteArray(4096))
        if (read > 0) {
            val buffer = ByteArray(read)
            byteChannel.readFully(buffer)
            byteList.addAll(buffer.toList())
        }
    }

    val uint8Array = Uint8Array(byteList.size)
    byteList.forEachIndexed { i, byte -> uint8Array[i] = byte.toUByte() }

    val blob = Blob(arrayOf(uint8Array.buffer), BlobPropertyBag(type = "application/octet-stream"))
    val url = js("URL.createObjectURL(blob)") as String

    val a = document.createElement("a") as HTMLAnchorElement
    a.href = url
    a.download = fileName
    a.style.display = "none"
    document.body?.appendChild(a)
    a.click()
    document.body?.removeChild(a)
}
