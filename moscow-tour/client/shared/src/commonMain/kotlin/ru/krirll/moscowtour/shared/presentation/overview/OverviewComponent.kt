package ru.krirll.moscowtour.shared.presentation.overview

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.instancekeeper.InstanceKeeper
import com.arkivanov.essenty.instancekeeper.getOrCreate
import com.arkivanov.essenty.lifecycle.coroutines.coroutineScope
import com.arkivanov.essenty.lifecycle.doOnStart
import com.arkivanov.essenty.lifecycle.doOnStop
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch
import org.koin.core.annotation.Factory
import ru.krirll.moscowtour.shared.di.factory.DispatcherProvider
import ru.krirll.moscowtour.shared.domain.SavedToursRepository
import ru.krirll.moscowtour.shared.domain.ToursApi
import ru.krirll.moscowtour.shared.domain.model.Tour
import ru.krirll.moscowtour.shared.presentation.RootComponent
import ru.krirll.moscowtour.shared.presentation.ShareManager
import ru.krirll.moscowtour.shared.presentation.createErrorHandler
import ru.krirll.moscowtour.shared.presentation.nav.Child
import ru.krirll.moscowtour.shared.presentation.nav.ComponentFactory
import ru.krirll.moscowtour.shared.presentation.nav.Route

class OverviewComponent(
    private val savedToursRepository: SavedToursRepository,
    private val toursApi: ToursApi,
    private val dispatcherProvider: DispatcherProvider,
    private val context: ComponentContext,
    private val id: Long,
    val doBack: () -> Unit,
    val buy: (Tour) -> Unit,
    val shareManager: ShareManager,
    private val snapshot: Snapshot = context.instanceKeeper.getOrCreate { Snapshot() }
) : ComponentContext by context {

    private val scope = coroutineScope()
    private val exceptionHandler = createErrorHandler(scope) {
        snapshot.errorCode.emit(it)
    }
    private var isSavedJob: Job? = null
    private val _isSaved = MutableStateFlow<Boolean?>(null)

    val details = snapshot.tour.asStateFlow()
    val errorCode = snapshot.errorCode.asSharedFlow()
    val isSaved = _isSaved.filterNotNull()

    init {
        doOnStart { loadIfNeeded() }
        doOnStop { isSavedJob?.cancel() }
    }

    fun save() {
        val details = snapshot.tour.value ?: return
        exec { savedToursRepository.save(details) }
    }

    fun remove() {
        val details = snapshot.tour.value ?: return
        exec { savedToursRepository.remove(details.id) }
    }

    fun loadIfNeeded() {
        listenIsSavedIfNeeded()
        if (snapshot.tour.value != null) {
            return
        }
        exec {
            snapshot.tour.emit(toursApi.fetchTours().first { it.id == id })
        }
    }

    private fun listenIsSavedIfNeeded() {
        if (isSavedJob == null || isSavedJob?.isActive == false) {
            isSavedJob = scope.launch(dispatcherProvider.main + exceptionHandler) {
                _isSaved.emitAll(savedToursRepository.isSaved(id))
            }
        }
    }

    private fun exec(callback: suspend () -> Unit): Job {
        return scope.launch(dispatcherProvider.main + exceptionHandler) {
            snapshot.errorCode.emit(null)
            callback.invoke()
        }
    }

    data class Snapshot(
        val tour: MutableStateFlow<Tour?> = MutableStateFlow(null),
        val errorCode: MutableSharedFlow<String?> = MutableSharedFlow(),
    ) : InstanceKeeper.Instance
}

@Factory(binds = [OverviewFactory::class])
class OverviewFactory(
    private val savedToursRepository: SavedToursRepository,
    private val toursApi: ToursApi,
    private val shareManager: ShareManager,
    private val dispatcherProvider: DispatcherProvider
) : ComponentFactory<Child.OverviewChild, Route.Overview> {

    override fun create(
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
            buy = { root.nav(Route.Overview.PersonScreen(it)) },
            doBack = { root.onBack() },
            shareManager = shareManager,
        )
        return Child.OverviewChild(comp)
    }
}
