package ru.krirll.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.materials.ExperimentalHazeMaterialsApi
import dev.chrisbanes.haze.materials.HazeMaterials
import dev.chrisbanes.haze.rememberHazeState

private const val BLUR_ENABLED = true

val LocalBlurState = compositionLocalOf<BlurState?> { null }

@Composable
fun rememberBlurState(): BlurState? {
    if (!BLUR_ENABLED) return null
    val hazeState = rememberHazeState()
    return remember(hazeState) { BlurState(hazeState) }
}

fun Modifier.applyBlurSource(state: BlurState?): Modifier {
    state ?: return this
    return hazeSource(state.hazeState)
}

@OptIn(ExperimentalHazeMaterialsApi::class)
@Composable
fun Modifier.applyBlurEffect(state: BlurState?): Modifier {
    state ?: return this
    return hazeEffect(state.hazeState, HazeMaterials.regular())
}

@Stable
class BlurState(internal val hazeState: HazeState)
