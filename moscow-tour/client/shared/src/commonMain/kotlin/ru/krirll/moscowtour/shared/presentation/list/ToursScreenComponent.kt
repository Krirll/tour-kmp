package ru.krirll.moscowtour.shared.presentation.list

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.instancekeeper.getOrCreate
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.koin.core.annotation.Factory
import ru.krirll.moscowtour.shared.di.factory.DispatcherProvider
import ru.krirll.moscowtour.shared.domain.ToursApi
import ru.krirll.moscowtour.shared.domain.model.Tour
import ru.krirll.moscowtour.shared.presentation.ListSnapshot
import ru.krirll.moscowtour.shared.presentation.RootComponent
import ru.krirll.moscowtour.shared.presentation.componentScope
import ru.krirll.moscowtour.shared.presentation.createErrorHandler
import ru.krirll.moscowtour.shared.presentation.nav.Child
import ru.krirll.moscowtour.shared.presentation.nav.ComponentFactory
import ru.krirll.moscowtour.shared.presentation.nav.Route
import ru.krirll.moscowtour.shared.presentation.state.TopAppBarStateHolder

class ToursScreenComponent(
    private val context: ComponentContext,
    private val toursApi: ToursApi,
    private val dispatcherProvider: DispatcherProvider,
    val topAppBarStateHolder: TopAppBarStateHolder,
    val search: String? = null,
    val showOverview: (Long) -> Unit,
    val doBack: () -> Unit
) : ComponentContext by context {
    private val snapshot = instanceKeeper.getOrCreate { ListSnapshot<Tour>() }
    private val exceptionHandler = createErrorHandler {
        snapshot.errorCode.emit(it)
    }
    val items: StateFlow<List<Tour>?> = snapshot.items
    val errorCode = snapshot.errorCode

    fun load() {
        componentScope.launch(dispatcherProvider.main + exceptionHandler) {
            snapshot.errorCode.emit(null)
            val rsp = toursApi.fetchTours() //todo сделать отбор по поиску и фильтрам
            snapshot.items.emit(rsp)
        }
    }
}

@Factory(binds = [ToursChildFactory::class])
class ToursChildFactory(
    private val topAppBarStateHolder: TopAppBarStateHolder,
    private val toursApi: ToursApi,
    private val dispatcherProvider: DispatcherProvider
) : ComponentFactory<Child.ToursChild, Route.Tours> {

    override fun create(
        route: Route.Tours,
        child: ComponentContext,
        root: RootComponent
    ): Child.ToursChild {
        val comp = ToursScreenComponent(
            child,
            toursApi,
            dispatcherProvider,
            topAppBarStateHolder,
            route.request,
            showOverview = { root.nav(Route.Overview(it))},
            doBack = { root.onBack() }
        )
        return Child.ToursChild(comp)
    }
}

