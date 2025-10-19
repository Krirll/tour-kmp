package ru.krirll.http.data

import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

actual class HttpClientFactory actual constructor(private val settings: HttpClientSettings) {
    actual fun create(): HttpClient {
        return HttpClient(OkHttp) {
            settings.setup(this)
            engine {
                config {
                    retryOnConnectionFailure(true)
                    connectTimeout(15, TimeUnit.SECONDS)
                    readTimeout(15, TimeUnit.SECONDS)
                    writeTimeout(15, TimeUnit.SECONDS)
                }
                preconfigured = OkHttpClient.Builder().pingInterval(30, TimeUnit.SECONDS).build()
            }
        }
    }
}
