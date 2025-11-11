package ru.krirll.moscowtour.shared.presentation.overview

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.instancekeeper.InstanceKeeper
import com.arkivanov.essenty.instancekeeper.getOrCreate
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
import ru.krirll.moscowtour.shared.domain.model.Tour
import ru.krirll.moscowtour.shared.presentation.RootComponent
import ru.krirll.moscowtour.shared.presentation.ShareManager
import ru.krirll.moscowtour.shared.presentation.componentScope
import ru.krirll.moscowtour.shared.presentation.createErrorHandler
import ru.krirll.moscowtour.shared.presentation.nav.Child
import ru.krirll.moscowtour.shared.presentation.nav.ComponentFactory
import ru.krirll.moscowtour.shared.presentation.nav.Route

class OverviewComponent(
    private val savedToursRepository: SavedToursRepository,
    private val dispatcherProvider: DispatcherProvider,
    private val context: ComponentContext,
    private val id: Long,
    val doBack: () -> Unit,
    val shareManager: ShareManager,
    private val snapshot: Snapshot = context.instanceKeeper.getOrCreate { Snapshot() }
) : ComponentContext by context {
    private val exceptionHandler = createErrorHandler {
        snapshot.errorCode.emit(it)
    }
    val details = snapshot.details.asStateFlow()
    val errorCode = snapshot.errorCode.asSharedFlow()
    val movieLink = snapshot.movieLink.asStateFlow()
    val movieUrl get() = movieLink.value?.first()?.url
    private var isSavedJob: Job? = null
    private val _isSaved = MutableStateFlow<Boolean?>(null)
    val isSaved = _isSaved.filterNotNull()

    init {
        doOnStart { init() }
        doOnStop { isSavedJob?.cancel() }
    }

    fun init() {
        loadDetailsIfNeeded()
    }

    private fun listenIsSavedIfNeeded() {
        if (isSavedJob == null || isSavedJob?.isActive == false) {
            isSavedJob = componentScope.launch(dispatcherProvider.main + exceptionHandler) {
                _isSaved.emitAll(savedToursRepository.isSaved(id))
            }
        }
    }

    fun save() {
        val details = snapshot.details.value ?: return
        exec { savedToursRepository.save(details) }
    }

    fun remove() {
        val details = snapshot.details.value ?: return
        exec { savedToursRepository.remove(details.id) }
    }

    fun loadDetailsIfNeeded() {
        listenIsSavedIfNeeded()
        if (snapshot.details.value != null) {
            return
        }
        exec {
            val details = interactor.fetchVideoDetails(id)
            if (details.isMovie) {
                val files = interactor.fetchVideoLinks(id, null)
                snapshot.movieLink.emit(files.files)
            }
            snapshot.details.emit(details)
        }
    }

    private fun exec(callback: suspend () -> Unit): Job {
        return componentScope.launch(dispatcherProvider.main + exceptionHandler) {
            snapshot.errorCode.emit(null)
            callback.invoke()
        }
    }

    data class Snapshot(
        val details: MutableStateFlow<Tour?> = MutableStateFlow(null),
        val errorCode: MutableSharedFlow<String?> = MutableSharedFlow(),
        val movieLink: MutableStateFlow<List<File>?> = MutableStateFlow(null)
    ) : InstanceKeeper.Instance
}

@Factory(binds = [OverviewFactory::class])
class OverviewFactory(
    private val savedToursRepository: SavedToursRepository,
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
            dispatcherProvider,
            child,
            route.id,
            doBack = { root.onBack() },
            shareManager = shareManager,
        )
        return Child.OverviewChild(comp)
    }
}

