package ru.krirll.moscowtour.shared.presentation.saved

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.lifecycle.coroutines.coroutineScope
import com.arkivanov.essenty.lifecycle.doOnStart
import com.arkivanov.essenty.lifecycle.doOnStop
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.koin.core.annotation.Factory
import ru.krirll.moscowtour.shared.di.factory.DispatcherProvider
import ru.krirll.moscowtour.shared.domain.SavedToursRepository
import ru.krirll.moscowtour.shared.domain.model.SavedTour
import ru.krirll.moscowtour.shared.domain.model.Tour
import ru.krirll.moscowtour.shared.presentation.RootComponent
import ru.krirll.moscowtour.shared.presentation.createErrorHandler
import ru.krirll.moscowtour.shared.presentation.nav.Child
import ru.krirll.moscowtour.shared.presentation.nav.ComponentFactory
import ru.krirll.moscowtour.shared.presentation.nav.Route
import kotlin.collections.map

class SavedMovieScreenComponent(
    private val context: ComponentContext,
    private val dispatcherProvider: DispatcherProvider,
    private val repo: SavedToursRepository,
    val doBack: () -> Unit,
    val showOverview: (Long) -> Unit
) : ComponentContext by context {

    private val scope = coroutineScope()
    private val _errorMsg = MutableStateFlow<String?>(null)
    private val exceptionHandler = createErrorHandler(scope) { _errorMsg.emit(it) }
    private val _all = MutableStateFlow<List<Tour>?>(null)

    private var prevJob: Job? = null

    val errorMsg = _errorMsg.asStateFlow()
    val all = _all.asStateFlow()

    init {
        lifecycle.doOnStop { prevJob?.cancel() }
        lifecycle.doOnStart { load() }
    }

    fun load() {
        prevJob?.cancel()
        prevJob = scope.launch(dispatcherProvider.main + exceptionHandler) {
            _errorMsg.emit(null)
            _all.emitAll(repo.getAll().map { it.map() })
        }
    }

    private fun List<SavedTour>.map(): List<Tour> {
        return this.map {
            with(it.tour) {
                Tour(
                    id,
                    title,
                    description,
                    city,
                    country,
                    dateBegin,
                    dateEnd,
                    canBuy,
                    price,
                    imagesUrls
                )
            }
        }
    }
}

@Factory(binds = [SavedMovieFactory::class])
class SavedMovieFactory(
    private val repo: SavedToursRepository,
    private val dispatcherProvider: DispatcherProvider,
) : ComponentFactory<Child.SavedToursChild, Route.Saved> {

    override fun create(
        route: Route.Saved,
        child: ComponentContext,
        root: RootComponent
    ): Child.SavedToursChild {
        val comp = SavedMovieScreenComponent(
            child,
            dispatcherProvider,
            repo,
            doBack = { root.onBack() },
            showOverview = { root.nav(Route.Overview(it)) }
        )
        return Child.SavedToursChild(comp)
    }
}
