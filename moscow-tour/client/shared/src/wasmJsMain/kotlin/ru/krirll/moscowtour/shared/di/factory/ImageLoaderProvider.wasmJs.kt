package ru.krirll.moscowtour.shared.di.factory

import com.seiko.imageloader.ImageLoader
import com.seiko.imageloader.component.setupDefaultComponents
import com.seiko.imageloader.intercept.bitmapMemoryCacheConfig

actual class ImageLoaderProvider actual constructor() {
    actual fun provide(): ImageLoader {
        return ImageLoader {
            components {
                setupDefaultComponents()
            }
            interceptor {
                bitmapMemoryCacheConfig {
                    maxSize(32 * 1024 * 1024)
                }
            }
        }
    }
}
