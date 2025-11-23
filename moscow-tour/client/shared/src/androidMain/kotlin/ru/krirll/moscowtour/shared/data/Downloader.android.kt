package ru.krirll.moscowtour.shared.data

actual suspend fun saveFileFromResponse(byteArray: ByteArray, fileName: String) {
    FileSaverAndroid.save(byteArray, fileName)
}
