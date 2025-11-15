package ru.krirll.moscowtour.shared.presentation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import moscowtour.moscow_tour.client.shared.generated.resources.Res
import moscowtour.moscow_tour.client.shared.generated.resources.back
import org.jetbrains.compose.resources.painterResource
import ru.krirll.moscowtour.shared.di.koin
import ru.krirll.moscowtour.shared.presentation.list.ToursScreen
import ru.krirll.moscowtour.shared.presentation.loading.LoadingScreen
import ru.krirll.moscowtour.shared.presentation.nav.Child
import ru.krirll.moscowtour.shared.presentation.nav.Route
import ru.krirll.moscowtour.shared.presentation.overview.OverviewScreen
import ru.krirll.moscowtour.shared.presentation.account.auth.AuthScreen
import ru.krirll.moscowtour.shared.presentation.account.pass.EditPasswordScreen
import ru.krirll.moscowtour.shared.presentation.account.register.RegisterScreen
import ru.krirll.ui.nav.Nav

@Composable
fun getColorScheme(): ColorScheme {
    return ru.krirll.ui.theme.getColorScheme()
}

@Composable
fun Modifier.imePaddingInternal(): Modifier {
    return koin.get<UiConfiguration>().imePadding(this)
}

@Composable
fun Nav(rootComponent: RootComponent) {
    NavInternal(rootComponent)
}

@Composable
private fun NavInternal(rootComponent: RootComponent) {
    Nav(rootComponent) { child, route ->
        when (child) {
            is Child.OverviewChild -> OverviewScreen(child.component)
            is Child.AuthChild -> AuthScreen(child.component)
            is Child.RegisterChild -> RegisterScreen(child.component)
            is Child.EditPasswordChild -> EditPasswordScreen(child.component)
            is Child.LoadingChild -> LoadingScreen(child.component)
            is Child.ToursChild,
            is Child.SearchChild,
            is Child.AccountChild,
            is Child.SavedToursChild -> {
                if (child is Child.ToursChild && child.component.search != null) {
                    ToursScreen(child.component)
                } else {
                    MainScreen(
                        route as Route,
                        child,
                        rootComponent
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SimpleAppBar(
    title: String,
    doBack: (() -> Unit)?,
    scrollBehavior: TopAppBarScrollBehavior? = null
) {
    TopAppBar(
        title = { Text(title) },
        navigationIcon = {
            doBack?.let { BackButton(it) }
        },
        scrollBehavior = scrollBehavior
    )
}

@Composable
fun BackButton(doBack: () -> Unit) {
    IconButton(onClick = { doBack() }) {
        Icon(painterResource(Res.drawable.back), contentDescription = null)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BaseScreen(
    scrollBehavior: TopAppBarScrollBehavior? = null,
    snackbarState: SnackbarHostState? = null,
    modifier: Modifier = Modifier,
    content: @Composable (padding: PaddingValues) -> Unit,
    appBar: @Composable () -> Unit
) {
    ru.krirll.ui.BaseScreen(
        modifier,
        scrollBehavior,
        snackbarState,
        getColorScheme(),
        content = content,
        appBar = { appBar() }
    )
}
