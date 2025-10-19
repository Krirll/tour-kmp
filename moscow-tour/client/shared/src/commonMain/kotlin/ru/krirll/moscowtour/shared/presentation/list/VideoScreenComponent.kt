package ru.krirll.moscowtour.shared.presentation.list

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.instancekeeper.getOrCreate
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.koin.core.annotation.Factory
import ru.krirll.moscowtour.shared.di.factory.DispatcherProvider
import ru.krirll.moscowtour.shared.domain.ToursApi
import ru.krirll.moscowtour.shared.presentation.ListSnapshot
import ru.krirll.moscowtour.shared.presentation.RootComponent
import ru.krirll.moscowtour.shared.presentation.componentScope
import ru.krirll.moscowtour.shared.presentation.createErrorHandler
import ru.krirll.moscowtour.shared.presentation.nav.Child
import ru.krirll.moscowtour.shared.presentation.nav.ComponentFactory
import ru.krirll.moscowtour.shared.presentation.nav.Route
import ru.krirll.moscowtour.shared.presentation.state.TopAppBarStateHolder

class VideoScreenComponent(
    private val context: ComponentContext,
    private val toursApi: ToursApi,
    private val dispatcherProvider: DispatcherProvider,
    val topAppBarStateHolder: TopAppBarStateHolder,
    val search: String? = null,
    val showOverview: (Long) -> Unit,
    val doBack: () -> Unit
) : ComponentContext by context {
    private val snapshot = instanceKeeper.getOrCreate { ListSnapshot<TourItem>() }
    private val exceptionHandler = createErrorHandler {
        snapshot.errorCode.emit(it)
    }
    val items: StateFlow<List<TourItem>?> = snapshot.items
    val errorCode = snapshot.errorCode

    fun load() {
        componentScope.launch(dispatcherProvider.main + exceptionHandler) {
            snapshot.errorCode.emit(null)
            val rsp = toursApi.fetchTours(search)
            snapshot.items.emit(rsp.items)
        }
    }
}

@Factory(binds = [VideosChildFactory::class])
class VideosChildFactory(
    private val topAppBarStateHolder: TopAppBarStateHolder,
    private val toursApi: ToursApi,
    private val dispatcherProvider: DispatcherProvider
) : ComponentFactory<Child.VideosChild, Route.Videos> {
    override fun create(
        route: Route.Videos,
        child: ComponentContext,
        root: RootComponent
    ): Child.VideosChild {
        val comp = VideoScreenComponent(
            child,
            toursApi,
            dispatcherProvider,
            topAppBarStateHolder,
            route.request,
            showOverview = { root.nav(Route.Overview(it))},
            doBack = { root.onBack() }
        )
        return Child.VideosChild(comp)
    }
}

