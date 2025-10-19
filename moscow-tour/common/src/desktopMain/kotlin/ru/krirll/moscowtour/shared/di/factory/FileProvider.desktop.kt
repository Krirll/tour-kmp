package ru.krirll.moscowtour.shared.di.factory

import java.io.File

actual class FileProvider {
    private val homeUser by lazy { System.getProperty("user.home") }
    private val appPackage = "ru.krirll.moscowtour"
    private var cacheDir: File? = null

    actual fun getCacheFileDir(): String? {
        cacheDir?.let { return it.absolutePath }
        return getCacheDir().apply {
            mkdirs()
            cacheDir = this
        }.absolutePath
    }

    private fun getCacheDir(): File {
        getMacOsCacheDir()?.let { return it }
        getLinuxCacheDir()?.let { return it }
        getWindowsCacheDir()?.let { return it }
        return File("cache") // fallback
    }

    private fun getMacOsCacheDir(): File? {
        return File(homeUser, "Library").provideCacheFile()
    }

    private fun getLinuxCacheDir(): File? {
        return File(homeUser, ".cache").provideCacheFile()
    }

    private fun getWindowsCacheDir(): File? {
        val appData = System.getenv("APPDATA")
        return File(appData).provideCacheFile()
    }

    private fun File.provideCacheFile(): File? {
        return if (exists() && canRead() && canWrite()) {
            File(this, appPackage)
        } else {
            null
        }
    }
}
