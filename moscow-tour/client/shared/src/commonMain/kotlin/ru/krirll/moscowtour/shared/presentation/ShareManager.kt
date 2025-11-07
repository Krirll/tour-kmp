package ru.krirll.moscowtour.shared.presentation

import ru.krirll.moscowtour.shared.domain.model.Tour

//todo сделать для ios
expect class ShareManager() {
    fun shareDetails(tour: Tour)
    fun canShare(): Boolean
}
