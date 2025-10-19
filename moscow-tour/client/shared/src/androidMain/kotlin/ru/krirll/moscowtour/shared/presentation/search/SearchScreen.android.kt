package ru.krirll.moscowtour.shared.presentation.search

import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type

actual fun Modifier.requestFocusOnDownEvent(focusRequester: FocusRequester): Modifier {
    return onKeyEvent {
        if (it.type == KeyEventType.KeyUp) {
            if (it.key == Key.DirectionDown) {
                focusRequester.requestFocus()
            }
        }
        true
    }
}
