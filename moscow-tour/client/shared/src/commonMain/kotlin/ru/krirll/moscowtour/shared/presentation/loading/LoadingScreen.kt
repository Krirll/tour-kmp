package ru.krirll.moscowtour.shared.presentation.loading

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import ru.krirll.moscowtour.shared.presentation.BaseScreen
import ru.krirll.moscowtour.shared.presentation.base.Loading

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoadingScreen(component: LoadingComponent) {
    LaunchedEffect(Unit) {
        component.onLoaded()
    }
    BaseScreen(
        appBar = {},
        content = { Loading() }
    )
}
