package ru.krirll.moscowtour.shared.data

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import ru.krirll.moscowtour.shared.domain.ServerConfiguration

fun ServerConfiguration.apply(
    builder: HttpRequestBuilder,
    path: String
) {
    builder.apply {
        url(asHttpStr() + "/$path")
    }
}

inline fun <reified T> HttpRequestBuilder.setJsonBody(body: T) {
    header("Content-Type","application/json")
    setBody(body)
}

suspend inline fun <reified T> HttpClient.get(
    path: String,
    params: Map<String, String>,
    configuration: ServerConfiguration
): T {
    val rsp = get {
        configuration.apply(this, path)
        params.forEach { (key, value) -> parameter(key, value) }
    }
    return rsp.body()
}
