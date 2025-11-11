package ru.krirll.moscowtour.shared.presentation

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.router.stack.childStackWebNavigation
import com.arkivanov.decompose.router.webhistory.WebNavigation
import com.arkivanov.decompose.router.webhistory.WebNavigationOwner
import kotlinx.coroutines.flow.map
import kotlinx.serialization.KSerializer
import org.koin.core.Koin
import org.koin.core.annotation.Factory
import ru.krirll.http.domain.TokenStorage
import ru.krirll.moscowtour.shared.di.koin
import ru.krirll.moscowtour.shared.domain.HeathCheck
import ru.krirll.moscowtour.shared.presentation.list.VideosChildFactory
import ru.krirll.moscowtour.shared.presentation.loading.LoadingComponentFactory
import ru.krirll.moscowtour.shared.presentation.nav.Child
import ru.krirll.moscowtour.shared.presentation.nav.ComponentFactory
import ru.krirll.moscowtour.shared.presentation.nav.Route
import ru.krirll.moscowtour.shared.presentation.overview.OverviewFactory
import ru.krirll.moscowtour.shared.presentation.overview.season.SeasonOverviewFactory
import ru.krirll.moscowtour.shared.presentation.saved.SavedMovieFactory
import ru.krirll.moscowtour.shared.presentation.search.SearchScreenFactory
import ru.krirll.moscowtour.shared.presentation.settings.SettingsComponentFactory
import ru.krirll.moscowtour.shared.presentation.settings.auth.AuthComponentFactory
import ru.krirll.moscowtour.shared.presentation.settings.pass.EditPasswordComponentFactory
import ru.krirll.moscowtour.shared.presentation.settings.register.RegisterComponentFactory
import ru.krirll.ui.nav.BaseRootComponent

@OptIn(ExperimentalDecomposeApi::class)
class RootComponent(
    componentContext: ComponentContext,
    private val koin: Koin,
    tokenStorage: TokenStorage,
    override val initStack: List<Route>,
    private val healthCheck: HeathCheck,
    override val serializer: KSerializer<Route> = Route.serializer()
) : BaseRootComponent<Route>(componentContext), WebNavigationOwner {
    val isLoggedIn = tokenStorage.token.map {
        if (it != null) {
            runCatching { healthCheck.check() }.getOrNull() ?: return@map false
            true
        } else {
            false
        }
    }

    override fun newChild(route: Route, ctx: ComponentContext): Child {
        val factory = route.provideFactory<Child, Route>(koin)
        return factory.create(route, ctx, this)
    }

    override val webNavigation: WebNavigation<*> =
        childStackWebNavigation(
            navigator = nav,
            stack = _childStack,
            serializer = Route.serializer(),
            pathMapper = { child ->
                UrlRoutes.build(child.configuration).path
            },
            parametersMapper = { child ->
                UrlRoutes.build(child.configuration).params
            }
        )
}

@Factory(binds = [RootFactory::class])
class RootFactory(
    private val tokenStorage: TokenStorage,
    private val heathCheck: HeathCheck
) {
    fun create(
        context: ComponentContext,
        initStack: Route
    ): RootComponent {
        return create(context, listOf(initStack))
    }

    fun create(
        context: ComponentContext,
        initStack: List<Route> = listOf(Route.default)
    ): RootComponent {
        return RootComponent(
            context,
            koin,
            tokenStorage,
            listOf(Route.Loading(next = initStack.toSet())),
            heathCheck
        )
    }
}

fun <T : Child, R : Route> Route.provideFactory(koin: Koin): ComponentFactory<T, R> {
    val factory = when (this) {
        is Route.Videos -> koin.get<VideosChildFactory>()
        is Route.SearchVideos -> koin.get<SearchScreenFactory>()
        is Route.Overview -> koin.get<OverviewFactory>()
        is Route.Overview.Season -> koin.get<SeasonOverviewFactory>()
        is Route.Settings -> koin.get<SettingsComponentFactory>()
        is Route.Settings.Auth -> koin.get<AuthComponentFactory>()
        is Route.Settings.Register -> koin.get<RegisterComponentFactory>()
        is Route.Saved -> koin.get<SavedMovieFactory>()
        is Route.Settings.EditPassword -> koin.get<EditPasswordComponentFactory>()
        is Route.Loading -> koin.get<LoadingComponentFactory>()
    }
    @Suppress("UNCHECKED_CAST") return factory as ComponentFactory<T, R>
}
