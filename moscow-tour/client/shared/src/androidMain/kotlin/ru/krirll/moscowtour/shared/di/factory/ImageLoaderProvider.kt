package ru.krirll.moscowtour.shared.di.factory

import android.app.Application
import com.seiko.imageloader.Bitmap
import com.seiko.imageloader.ImageLoader
import com.seiko.imageloader.cache.memory.MemoryCacheBuilder
import com.seiko.imageloader.cache.memory.MemoryKey
import com.seiko.imageloader.cache.memory.maxSizePercent
import com.seiko.imageloader.component.setupDefaultComponents
import com.seiko.imageloader.intercept.bitmapMemoryCacheConfig
import com.seiko.imageloader.option.androidContext
import com.seiko.imageloader.size
import com.seiko.imageloader.util.identityHashCode
import io.ktor.client.HttpClient
import okio.Path.Companion.toOkioPath
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

actual class ImageLoaderProvider : KoinComponent {
    private val app: Application = get()
    private val httpClient: HttpClient = get()

    actual fun provide(): ImageLoader {
        return ImageLoader {
            options {
                androidContext(app)
            }
            components {
                setupDefaultComponents { httpClient }
            }
            interceptor {
                // Set the max size to 25% of the app's available memory.
                bitmapMemoryCacheConfig(
                    valueHashProvider = { identityHashCode(it) },
                    valueSizeProvider = { it.size },
                    block = fun MemoryCacheBuilder<MemoryKey, Bitmap>.() {
                        // Set the max size to 25% of the app's available memory.
                        maxSizePercent(app, 0.25)
                    }
                )
                diskCacheConfig {
                    directory(app.cacheDir.resolve("image_cache").toOkioPath())
                    maxSizeBytes(512L * 1024 * 1024) // 512MB
                }
            }
        }
    }
}
