package ru.krirll.moscowtour.shared.presentation.state

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.saveable.rememberSaveable
import org.koin.core.annotation.Factory

@Factory
class TopAppBarStateHolder {
    var heightOffsetLimit: Float = -Float.MAX_VALUE
    var heightOffset: Float = 0f
    var contentOffset: Float = 0f
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun rememberTopAppBarStateByHolder(holder: TopAppBarStateHolder): TopAppBarState {
    val state = rememberSaveable(saver = TopAppBarState.Saver) {
        TopAppBarState(
            holder.heightOffsetLimit,
            holder.heightOffset,
            holder.contentOffset
        )
    }
    DisposableEffect(Unit) {
        onDispose {
            holder.heightOffsetLimit = state.heightOffsetLimit
            holder.heightOffset = state.heightOffset
            holder.contentOffset = state.contentOffset
        }
    }
    return state
}
