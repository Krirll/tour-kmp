package ru.krirll.moscowtour.shared.data

import kotlinx.coroutines.withContext
import okio.Buffer
import okio.ByteString.Companion.toByteString
import org.koin.core.annotation.Factory
import ru.krirll.moscowtour.shared.di.factory.DispatcherProvider

@Factory
class HmacSha512Calc(private val dispatcherProvider: DispatcherProvider) {
    private val hmacKey = "cd4204fae910ba12b9eb6650ef90852252cf20e4c55bbb3b21ac1f8cdf8d0ab53a5f49d2298824f011dbefd30fdec21ff0b485ae835dd9bccc9d377ed54c0d80"

    @OptIn(ExperimentalStdlibApi::class)
    suspend fun calc(data: ByteArray): ByteArray = withContext(dispatcherProvider.io) {
        val buffer = Buffer().apply { write(data) }
        val hmacStr = hmacKey.hexToByteArray().toByteString()
        buffer.hmacSha512(hmacStr).toByteArray()
    }
}
