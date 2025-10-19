package ru.krirll.http.data

import android.content.Context
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import okhttp3.OkHttpClient
import org.koin.core.qualifier.named
import ru.krirll.http.R
import ru.krirll.koin
import java.security.KeyStore
import java.security.SecureRandom
import java.security.cert.CertificateException
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.TrustManagerFactory
import javax.net.ssl.X509TrustManager

actual class HttpClientFactory actual constructor(private val settings: HttpClientSettings) {
    private val context by koin.inject<Context>()
    private val useHttpCustomCerts: Boolean? by koin.injectOrNull(named(USE_HTTP_CUSTOM_CERTS))

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
                preconfigured = if (useHttpCustomCerts ?: true) {
                    buildOkHttpClientWithSystemAndCustomCA()
                } else {
                    OkHttpClient.Builder()
                }.pingInterval(30, TimeUnit.SECONDS)
                        .build()
            }
        }
    }

    private fun buildOkHttpClientWithSystemAndCustomCA(): OkHttpClient.Builder {
        // Загружаем системные CA
        val defaultTmFactory =
            TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())
        defaultTmFactory.init(null as? KeyStore)
        val defaultTrustManagers = defaultTmFactory.trustManagers
        val defaultTM = defaultTrustManagers.first { it is X509TrustManager } as X509TrustManager

        // Загружаем кастомный CA
        val certificateFactory = CertificateFactory.getInstance("X.509")
        val caInput = context.resources.openRawResource(R.raw.x1)
        val customCA = caInput.use { certificateFactory.generateCertificate(it) }

        val customKeyStore = KeyStore.getInstance(KeyStore.getDefaultType()).apply {
            load(null, null)
            setCertificateEntry("custom-ca", customCA)
        }

        val customTmFactory =
            TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())
        customTmFactory.init(customKeyStore)
        val customTM =
            customTmFactory.trustManagers.first { it is X509TrustManager } as X509TrustManager

        // Объединяем оба TrustManager'а
        val combinedTM = CombinedTrustManager(defaultTM, customTM)

        val sslContext = SSLContext.getInstance("TLS").apply {
            init(null, arrayOf<TrustManager>(combinedTM), SecureRandom())
        }

        return OkHttpClient.Builder().sslSocketFactory(sslContext.socketFactory, combinedTM)
    }

    private class CombinedTrustManager(
        private val defaultTM: X509TrustManager, private val customTM: X509TrustManager
    ) : X509TrustManager {
        override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {
            try {
                defaultTM.checkClientTrusted(chain, authType)
            } catch (e: CertificateException) {
                customTM.checkClientTrusted(chain, authType)
            }
        }

        override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {
            try {
                defaultTM.checkServerTrusted(chain, authType)
            } catch (e: CertificateException) {
                customTM.checkServerTrusted(chain, authType)
            }
        }

        override fun getAcceptedIssuers(): Array<X509Certificate> =
            defaultTM.acceptedIssuers + customTM.acceptedIssuers
    }
}
