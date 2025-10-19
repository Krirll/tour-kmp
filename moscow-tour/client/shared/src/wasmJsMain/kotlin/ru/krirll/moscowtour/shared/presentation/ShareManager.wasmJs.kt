package ru.krirll.moscowtour.shared.presentation

import ru.krirll.moscowtour.shared.domain.model.Tour

actual class ShareManager actual constructor() {
    actual fun shareDetails(details: Tour) {
    }

    actual fun canShare(): Boolean {
        return false
    }
}