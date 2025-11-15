package ru.krirll.moscowtour.shared.presentation.search

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.instancekeeper.InstanceKeeper
import com.arkivanov.essenty.instancekeeper.getOrCreate
import com.arkivanov.essenty.lifecycle.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.koin.core.annotation.Factory
import org.koin.core.annotation.Named
import ru.krirll.domain.Log
import ru.krirll.moscowtour.shared.di.factory.DispatcherProvider
import ru.krirll.moscowtour.shared.domain.EventType
import ru.krirll.moscowtour.shared.domain.RemoteEvent
import ru.krirll.moscowtour.shared.domain.RemoteEventListener
import ru.krirll.moscowtour.shared.domain.SearchRepository
import ru.krirll.moscowtour.shared.presentation.RootComponent
import ru.krirll.moscowtour.shared.presentation.createErrorHandler
import ru.krirll.moscowtour.shared.presentation.nav.Child
import ru.krirll.moscowtour.shared.presentation.nav.ComponentFactory
import ru.krirll.moscowtour.shared.presentation.nav.Route

class SearchScreenComponent(
    private val context: ComponentContext,
    val search: (String) -> Unit,
    val doBack: () -> Unit,
    private val dispatcherProvider: DispatcherProvider,
    private val searchRepository: SearchRepository,
    remoteEventListener: RemoteEventListener,
    private val log: Log,
    private val snapshot: Snapshot = context.instanceKeeper.getOrCreate { Snapshot() }
) : ComponentContext by context {

    val oldSearch = snapshot.oldSearch.asStateFlow()
    val searchEvent = remoteEventListener.event.filter { it is RemoteEvent.OnSearch }
        .catch {
            log.d("SearchScreenComponent", "error in screen", it)
            snapshot.errorMsg.emit(it.stackTraceToString())
        }
    val requestCache get() = snapshot.requestCache
    val errorMsg get() = snapshot.errorMsg

    val isLoading get() = snapshot.isLoading

    private val scope = coroutineScope()
    private var lastCmd: (suspend () -> Unit)? = null
    private val exceptionHandler = createErrorHandler(scope) {
        snapshot.errorMsg.emit(it)
    }

    fun refresh() {
        exec { snapshot.oldSearch.emit(searchRepository.getAll().first()) }
    }

    fun restart() {
        scope.launch(dispatcherProvider.main + exceptionHandler) {
            snapshot.errorMsg.emit(null)
            snapshot.isLoading.emit(true)
            try {
                lastCmd?.invoke()
                lastCmd = null
            } finally {
                snapshot.isLoading.emit(false)
            }
        }
    }

    fun onValueChange(request: String) {
        snapshot.requestCache = request
    }

    fun done() = exec {
        if (requestCache.isNotBlank()) {
            searchRepository.addToSearch(requestCache)
            log.d("SearchScreenComponent", "request cache emit $requestCache")
            search(requestCache)
        }
    }

    fun removeFromSearch(item: String) = exec {
        searchRepository.delete(item)
        snapshot.oldSearch.emit(searchRepository.getAll().first())
    }

    private fun exec(callback: suspend () -> Unit) {
        scope.launch(dispatcherProvider.main + exceptionHandler) {
            try {
                snapshot.isLoading.emit(true)
                lastCmd = callback
                callback.invoke()
                lastCmd = null
            } finally {
                snapshot.isLoading.emit(false)
            }
        }
    }

    data class Snapshot(
        var requestCache: String = "",
        val oldSearch: MutableStateFlow<List<String>> = MutableStateFlow(emptyList()),
        val doSearch: MutableSharedFlow<String?> = MutableSharedFlow(),
        val errorMsg: MutableStateFlow<String?> = MutableStateFlow(null),
        val isLoading: MutableStateFlow<Boolean> = MutableStateFlow(false)
    ) : InstanceKeeper.Instance
}

@Factory(binds = [SearchScreenFactory::class])
class SearchScreenFactory(
    private val dispatcherProvider: DispatcherProvider,
    private val searchRepository: SearchRepository,
    @Named(EventType.SEARCH) private val remoteEventListener: RemoteEventListener,
    private val log: Log
) : ComponentFactory<Child.SearchChild, Route.SearchTours> {

    override fun create(
        route: Route.SearchTours,
        child: ComponentContext,
        root: RootComponent
    ): Child.SearchChild {
        val comp = SearchScreenComponent(
            child,
            search = { root.nav(Route.Tours(it)) },
            doBack = { root.onBack() },
            dispatcherProvider,
            searchRepository,
            remoteEventListener,
            log
        )
        return Child.SearchChild(comp)
    }
}

