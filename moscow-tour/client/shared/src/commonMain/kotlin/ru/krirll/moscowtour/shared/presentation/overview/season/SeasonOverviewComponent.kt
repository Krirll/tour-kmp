package ru.krirll.moscowtour.shared.presentation.overview.season

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.instancekeeper.getOrCreate
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.koin.core.annotation.Factory
import ru.krirll.moscowtour.shared.di.factory.DispatcherProvider
import ru.krirll.moscowtour.shared.domain.interactor.AboutVideoInteractor
import ru.krirll.moscowtour.shared.presentation.ListSnapshot
import ru.krirll.moscowtour.shared.presentation.RootComponent
import ru.krirll.moscowtour.shared.presentation.componentScope
import ru.krirll.moscowtour.shared.presentation.createErrorHandler
import ru.krirll.moscowtour.shared.presentation.nav.Child
import ru.krirll.moscowtour.shared.presentation.nav.ComponentFactory
import ru.krirll.moscowtour.shared.presentation.nav.Route

class SeasonOverviewComponent(
    private val interactor: AboutVideoInteractor,
    private val context: ComponentContext,
    private val dispatchers: DispatcherProvider,
    val id: Long,
    val showEpisode: (seasonId: Long) -> Unit,
    val doBack: () -> Unit,
    private val snapshot: ListSnapshot<Season> = context.instanceKeeper.getOrCreate { ListSnapshot() }
) : ComponentContext by context {
    private val exceptionHandler = createErrorHandler {
        snapshot.errorCode.emit(it)
    }
    val season = snapshot.items.asStateFlow()
    val errorMessage = snapshot.errorCode.asSharedFlow()

    fun load() {
        componentScope.launch(dispatchers.main + exceptionHandler) {
            snapshot.errorCode.emit(null)
            if (snapshot.items.value.isNullOrEmpty()) {
                val links = interactor.fetchVideoLinks(id)
                snapshot.items.emit(links.seasons)
            }
        }
    }
}

@Factory(binds = [SeasonOverviewFactory::class])
class SeasonOverviewFactory(
    private val dispatcherProvider: DispatcherProvider,
    private val interactor: AboutVideoInteractor,
) : ComponentFactory<Child.SeasonOverviewChild, Route.Overview.Season> {
    override fun create(
        route: Route.Overview.Season,
        child: ComponentContext,
        root: RootComponent
    ): Child.SeasonOverviewChild {
        val comp = SeasonOverviewComponent(
            interactor,
            child,
            dispatcherProvider,
            route.id,
            showEpisode = { root.nav(Route.Overview.Episode(route.id, it)) },
            doBack = { root.onBack() }
        )
        return Child.SeasonOverviewChild(comp)
    }
}

