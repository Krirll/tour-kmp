package ru.krirll.moscowtour.shared.presentation

import ru.krirll.moscowtour.shared.domain.model.Tour

expect class ShareManager() {
    fun shareDetails(details: Tour)
    fun canShare(): Boolean
}
