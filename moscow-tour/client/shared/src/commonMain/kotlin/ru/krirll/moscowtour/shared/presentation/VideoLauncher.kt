package ru.krirll.moscowtour.shared.presentation

expect open class VideoLauncher() {
    open suspend fun launch(uri: String)
}
