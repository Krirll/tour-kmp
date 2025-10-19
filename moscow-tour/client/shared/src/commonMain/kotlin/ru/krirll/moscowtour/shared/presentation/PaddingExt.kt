package ru.krirll.moscowtour.shared.presentation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection

@Composable
fun Modifier.applyColumnPadding(paddingValues: PaddingValues): Modifier {
    return padding(
        start = paddingValues.calculateStartPadding(LocalLayoutDirection.current),
        top = paddingValues.calculateTopPadding(),
        end = paddingValues.calculateEndPadding(LocalLayoutDirection.current),
    )
}

@Composable
fun PaddingValues.asColumnPadding(): PaddingValues {
    return PaddingValues(
        bottom = calculateBottomPadding()
    )
}
