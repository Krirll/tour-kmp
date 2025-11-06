package ru.krirll.moscowtour.shared.data

import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsChannel
import io.ktor.http.HttpHeaders
import io.ktor.utils.io.ByteReadChannel

expect suspend fun saveFileFromResponse(byteChannel: ByteReadChannel, fileName: String)

suspend fun saveFileFromResponse(response: HttpResponse) {
    val contentDisposition = response.headers[HttpHeaders.ContentDisposition]
    val fileName = contentDisposition
        ?.substringAfter("filename=\"")
        ?.substringBefore("\"")
        ?: "билет.docx"
    saveFileFromResponse(response.bodyAsChannel(), fileName)
}
