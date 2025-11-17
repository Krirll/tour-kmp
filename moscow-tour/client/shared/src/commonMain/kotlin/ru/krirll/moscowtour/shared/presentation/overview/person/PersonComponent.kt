package ru.krirll.moscowtour.shared.presentation.overview.person

import com.arkivanov.decompose.ComponentContext
import org.koin.core.annotation.Factory
import ru.krirll.moscowtour.shared.di.factory.DispatcherProvider
import ru.krirll.moscowtour.shared.domain.SavedToursRepository
import ru.krirll.moscowtour.shared.domain.ToursApi
import ru.krirll.moscowtour.shared.presentation.RootComponent
import ru.krirll.moscowtour.shared.presentation.ShareManager
import ru.krirll.moscowtour.shared.presentation.nav.Child
import ru.krirll.moscowtour.shared.presentation.nav.ComponentFactory
import ru.krirll.moscowtour.shared.presentation.nav.Route

class PersonComponent(
    private val context: ComponentContext,
) : ComponentContext by context {

}

@Factory(binds = [PersonFactory::class])
class PersonFactory(
    private val savedToursRepository: SavedToursRepository,
    private val toursApi: ToursApi,
    private val shareManager: ShareManager,
    private val dispatcherProvider: DispatcherProvider
) : ComponentFactory<Child.OverviewChild, Route.Account.Tickets> {

    /*    override fun create(
            route: Route.Overview,
            child: ComponentContext,
            root: RootComponent
        ): Child.OverviewChild {
            val comp = OverviewComponent(
                savedToursRepository,
                toursApi,
                dispatcherProvider,
                child,
                route.id,
                doBack = { root.onBack() },
                shareManager = shareManager,
            )
            return Child.OverviewChild(comp)
        }*/

    override fun create(
        route: Route.Account.Tickets,
        child: ComponentContext,
        root: RootComponent
    ): Child.OverviewChild {
        TODO("Not yet implemented")
    }

}
