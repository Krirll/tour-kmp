package ru.krirll.moscowtour.shared.di.factory

import com.seiko.imageloader.ImageLoader

//todo хуй знает надо или нет
expect class ImageLoaderProvider() {
    fun provide(): ImageLoader
}
