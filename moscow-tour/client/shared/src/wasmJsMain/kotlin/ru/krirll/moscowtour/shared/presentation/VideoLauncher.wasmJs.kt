package ru.krirll.moscowtour.shared.presentation

import kotlinx.browser.window

actual open class VideoLauncher actual constructor() {
    actual open suspend fun launch(uri: String) {
        window.open(uri, "_blank", "noreferrer")
    }
}
