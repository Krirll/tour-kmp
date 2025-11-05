package ru.krirll.moscowtour.shared.data

import kotlinx.coroutines.withContext
import okio.Buffer
import okio.ByteString.Companion.toByteString
import org.koin.core.annotation.Factory
import ru.krirll.moscowtour.shared.di.factory.DispatcherProvider

@Factory
class HmacSha512Calc(private val dispatcherProvider: DispatcherProvider) {
    private val hmacKey = "dbdc444b27579eaef1fe84e77e6161cba649eca0885a618b1ff2f5177563d6c808ba414d759ed9b5ac03acf460310b93e43f62688f05ac5ecf4327cf0bf61379"

    @OptIn(ExperimentalStdlibApi::class)
    suspend fun calc(data: ByteArray): ByteArray = withContext(dispatcherProvider.io) {
        val buffer = Buffer().apply { write(data) }
        val hmacStr = hmacKey.hexToByteArray().toByteString()
        buffer.hmacSha512(hmacStr).toByteArray()
    }
}
