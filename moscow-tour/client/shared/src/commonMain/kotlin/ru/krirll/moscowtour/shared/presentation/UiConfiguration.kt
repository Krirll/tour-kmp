package ru.krirll.moscowtour.shared.presentation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.koin.core.annotation.Factory
import org.koin.core.annotation.Singleton

@Singleton
class UiConfiguration {
    @Composable
    fun imePadding(modifier: Modifier): Modifier {
        return modifier
    }
}
