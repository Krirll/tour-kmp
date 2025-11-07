package ru.krirll.moscowtour.shared.presentation

import ru.krirll.moscowtour.shared.domain.model.Tour

actual class ShareManager actual constructor() {
    actual fun shareDetails(tour: Tour) = Unit
    actual fun canShare(): Boolean = false
}
