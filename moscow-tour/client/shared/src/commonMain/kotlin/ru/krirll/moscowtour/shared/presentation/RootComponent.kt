package ru.krirll.moscowtour.shared.presentation

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.ExperimentalDecomposeApi
import kotlinx.coroutines.flow.map
import kotlinx.serialization.KSerializer
import org.koin.core.Koin
import org.koin.core.annotation.Factory
import ru.krirll.http.domain.TokenStorage
import ru.krirll.moscowtour.shared.di.koin
import ru.krirll.moscowtour.shared.domain.HeathCheck
import ru.krirll.moscowtour.shared.presentation.account.AccountComponentFactory
import ru.krirll.moscowtour.shared.presentation.account.auth.AuthComponentFactory
import ru.krirll.moscowtour.shared.presentation.account.pass.EditPasswordComponentFactory
import ru.krirll.moscowtour.shared.presentation.account.register.RegisterComponentFactory
import ru.krirll.moscowtour.shared.presentation.account.tickets.TicketsFactory
import ru.krirll.moscowtour.shared.presentation.list.ToursChildFactory
import ru.krirll.moscowtour.shared.presentation.loading.LoadingComponentFactory
import ru.krirll.moscowtour.shared.presentation.nav.Child
import ru.krirll.moscowtour.shared.presentation.nav.ComponentFactory
import ru.krirll.moscowtour.shared.presentation.nav.Route
import ru.krirll.moscowtour.shared.presentation.overview.FullscreenImageCarouselChildFactory
import ru.krirll.moscowtour.shared.presentation.overview.OverviewFactory
import ru.krirll.moscowtour.shared.presentation.overview.buy.BuyFactory
import ru.krirll.moscowtour.shared.presentation.overview.person.PersonFactory
import ru.krirll.moscowtour.shared.presentation.saved.SavedToursFactory
import ru.krirll.moscowtour.shared.presentation.search.SearchScreenFactory
import ru.krirll.ui.nav.BaseRootComponent

@OptIn(ExperimentalDecomposeApi::class)
class RootComponent(
    componentContext: ComponentContext,
    private val koin: Koin,
    tokenStorage: TokenStorage,
    override val initStack: List<Route>,
    private val healthCheck: HeathCheck,
    override val serializer: KSerializer<Route> = Route.serializer()
) : BaseRootComponent<Route>(componentContext) {
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
        is Route.Tours -> koin.get<ToursChildFactory>()
        is Route.SearchTours -> koin.get<SearchScreenFactory>()
        is Route.Overview -> koin.get<OverviewFactory>()
        is Route.Overview.PersonScreen -> koin.get<PersonFactory>()
        is Route.Overview.BuyTicket -> koin.get<BuyFactory>()
        is Route.Overview.FullscreenImages -> koin.get<FullscreenImageCarouselChildFactory>()
        is Route.Account -> koin.get<AccountComponentFactory>()
        is Route.Account.Auth -> koin.get<AuthComponentFactory>()
        is Route.Account.Register -> koin.get<RegisterComponentFactory>()
        is Route.Account.Tickets -> koin.get<TicketsFactory>()
        is Route.Account.EditPassword -> koin.get<EditPasswordComponentFactory>()
        is Route.Saved -> koin.get<SavedToursFactory>()
        is Route.Loading -> koin.get<LoadingComponentFactory>()
    }
    @Suppress("UNCHECKED_CAST") return factory as ComponentFactory<T, R>
}
