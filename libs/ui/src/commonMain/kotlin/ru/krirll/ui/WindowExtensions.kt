package ru.krirll.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo

@Composable
private fun currentWindowWidthDp(): Int {
    val info = LocalWindowInfo.current
    val density = LocalDensity.current
    return with(density) { info.containerSize.width.toDp().value.toInt() }
}

@Composable
fun currentWindowType(): WindowType {
    return widthToSizeClass(currentWindowWidthDp())
}

sealed interface WindowType {
    object Compact : WindowType
    object Expanded : WindowType
}

val WindowType.isExpanded: Boolean get() = this is WindowType.Expanded
val WindowType.isCompact: Boolean get() = this is WindowType.Compact

private fun widthToSizeClass(widthDp: Int): WindowType = when {
    widthDp >= 840 -> WindowType.Expanded
    else -> WindowType.Compact
}
