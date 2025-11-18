package ru.krirll.moscowtour.shared.presentation.base

sealed interface ScreenState {
    data object Idle : ScreenState
    data object Loading : ScreenState
    data class Error(val e: Throwable) : ScreenState
    data object Succeed : ScreenState
}
