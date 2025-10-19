package ru.krirll.moscowtour.shared.di.factory

import com.seiko.imageloader.ImageLoader

expect class ImageLoaderProvider() {
    fun provide(): ImageLoader
}
