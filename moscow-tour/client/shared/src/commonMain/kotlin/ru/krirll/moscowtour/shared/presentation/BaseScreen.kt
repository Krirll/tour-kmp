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
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.seiko.imageloader.LocalImageLoader
import moscowtour.moscow_tour.client.shared.generated.resources.Res
import moscowtour.moscow_tour.client.shared.generated.resources.back
import org.jetbrains.compose.resources.painterResource
import ru.krirll.ui.nav.Nav
import ru.krirll.moscowtour.shared.di.factory.ImageLoaderProvider
import ru.krirll.moscowtour.shared.di.koin
import ru.krirll.moscowtour.shared.presentation.list.VideoScreen
import ru.krirll.moscowtour.shared.presentation.loading.LoadingScreen
import ru.krirll.moscowtour.shared.presentation.nav.Child
import ru.krirll.moscowtour.shared.presentation.nav.Route
import ru.krirll.moscowtour.shared.presentation.overview.OverviewScreen
import ru.krirll.moscowtour.shared.presentation.overview.episode.EpisodesOverviewScreen
import ru.krirll.moscowtour.shared.presentation.overview.season.SeasonOverviewScreen
import ru.krirll.moscowtour.shared.presentation.settings.auth.AuthScreen
import ru.krirll.moscowtour.shared.presentation.settings.pass.EditPasswordScreen
import ru.krirll.moscowtour.shared.presentation.settings.register.RegisterScreen
import ru.krirll.moscowtour.shared.presentation.settings.serv.EditServScreen

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
    CompositionLocalProvider(
        LocalImageLoader provides remember { ImageLoaderProvider().provide() }
    ) {
        NavInternal(rootComponent)
    }
}

@Composable
private fun NavInternal(rootComponent: RootComponent) {
    Nav(rootComponent) { child, route ->
        when (child) {
            is Child.OverviewChild -> OverviewScreen(child.component)
            is Child.SeasonOverviewChild -> SeasonOverviewScreen(child.component)
            is Child.EpisodeOverviewChild -> EpisodesOverviewScreen(child.component)
            is Child.EditServerAddrChild -> EditServScreen(child.component)
            is Child.AuthChild -> AuthScreen(child.component)
            is Child.RegisterChild -> RegisterScreen(child.component)
            is Child.EditPasswordChild -> EditPasswordScreen(child.component)
            is Child.LoadingChild -> LoadingScreen(child.component)
            is Child.VideosChild,
            is Child.SearchChild,
            is Child.SettingsChild,
            is Child.SavedMovieChild -> {
                if (child is Child.VideosChild && child.component.search != null) {
                    VideoScreen(child.component)
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
