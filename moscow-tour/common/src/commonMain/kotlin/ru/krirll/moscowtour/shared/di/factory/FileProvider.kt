package ru.krirll.moscowtour.shared.di.factory

expect class FileProvider() {
    fun getCacheFileDir(): String?
}
