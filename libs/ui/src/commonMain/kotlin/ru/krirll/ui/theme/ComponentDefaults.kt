package ru.krirll.ui.theme

import androidx.compose.material3.AppBarWithSearchColors
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import ru.krirll.ui.LocalBlurState

object ComponentDefaults {

    @Composable
    fun topAppBarColors(): TopAppBarColors {
        return if (isBlurDisabled()) {
            TopAppBarDefaults.topAppBarColors()
        } else {
            TopAppBarDefaults.topAppBarColors(
                Color.Transparent,
                Color.Transparent
            )
        }
    }

    @Composable
    fun navBarContentColor(): Color {
        return if (isBlurDisabled()) {
            NavigationBarDefaults.containerColor
        } else {
            Color.Transparent
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun appBarWithSearchDefaults(): AppBarWithSearchColors {
        return if (isBlurDisabled()) {
            SearchBarDefaults.appBarWithSearchColors()
        } else {
            SearchBarDefaults.appBarWithSearchColors(
                appBarContainerColor = Color.Transparent
            )
        }
    }

    @Composable
    private fun isBlurDisabled(): Boolean {
        return LocalBlurState.current == null
    }
}
