package ru.krirll.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.lifecycle.Lifecycle
import com.arkivanov.essenty.lifecycle.Lifecycle.State
import com.arkivanov.essenty.lifecycle.coroutines.withLifecycle
import kotlinx.coroutines.flow.Flow

@Composable
fun <T> Flow<T>.collectAsStateWithLifecycle(
    lifecycle: Lifecycle,
    initial: T,
    minActiveState: State = State.STARTED
): androidx.compose.runtime.State<T> {
    return withLifecycle(lifecycle, minActiveState)
        .collectAsState(initial)
}

@Composable
fun <T> Flow<T>.collectAsStateWithLifecycle(
    componentContext: ComponentContext,
    initial: T,
    minActiveState: State = State.STARTED
): androidx.compose.runtime.State<T> {
    return collectAsStateWithLifecycle(componentContext.lifecycle, initial, minActiveState)
}
