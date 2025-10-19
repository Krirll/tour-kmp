package ru.krirll.moscowtour.shared.presentation.loading

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import ru.krirll.moscowtour.shared.presentation.BaseScreen
import ru.krirll.moscowtour.shared.presentation.list.Loading

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoadingScreen(component: LoadingComponent) {
    val isLoggedIn by component.isLoggedIn.collectAsState(null)
    LaunchedEffect(isLoggedIn) {
        isLoggedIn?.let { component.onLoaded(it) }
    }
    BaseScreen(
        appBar = {},
        content = { Loading() }
    )
}
