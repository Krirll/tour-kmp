package ru.krirll.moscowtour.shared.domain

import kotlinx.coroutines.flow.StateFlow

interface ActivityInfo {
    val isActiveFlow: StateFlow<Boolean>
}
