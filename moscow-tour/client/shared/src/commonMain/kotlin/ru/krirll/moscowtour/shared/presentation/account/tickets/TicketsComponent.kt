package ru.krirll.moscowtour.shared.presentation.account.tickets

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.lifecycle.coroutines.coroutineScope
import com.arkivanov.essenty.lifecycle.doOnStart
import com.arkivanov.essenty.lifecycle.doOnStop
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.launch
import org.koin.core.annotation.Factory
import ru.krirll.moscowtour.shared.di.factory.DispatcherProvider
import ru.krirll.moscowtour.shared.domain.TicketsRepository
import ru.krirll.moscowtour.shared.domain.model.Ticket
import ru.krirll.moscowtour.shared.presentation.RootComponent
import ru.krirll.moscowtour.shared.presentation.createErrorHandler
import ru.krirll.moscowtour.shared.presentation.nav.Child
import ru.krirll.moscowtour.shared.presentation.nav.ComponentFactory
import ru.krirll.moscowtour.shared.presentation.nav.Route

class TicketsComponent(
    private val context: ComponentContext,
    private val dispatcherProvider: DispatcherProvider,
    private val ticketsRepository: TicketsRepository,
    val onOverview: (Long) -> Unit,
    val onBack: () -> Unit,
) : ComponentContext by context {

    private val scope = coroutineScope(SupervisorJob())
    private val _errorMsg = MutableStateFlow<String?>(null)
    private val exceptionHandler = createErrorHandler(scope) { _errorMsg.emit(it) }
    private val _all = MutableStateFlow<List<Ticket>?>(null)

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
            _all.emitAll(ticketsRepository.getAll())
        }
    }

}

@Factory(binds = [TicketsFactory::class])
class TicketsFactory(
    private val ticketsRepository: TicketsRepository,
    private val dispatcherProvider: DispatcherProvider
) : ComponentFactory<Child.TicketsChild, Route.Account.Tickets> {

    override fun create(
        route: Route.Account.Tickets,
        child: ComponentContext,
        root: RootComponent
    ): Child.TicketsChild {
        return Child.TicketsChild(
            TicketsComponent(
                child,
                dispatcherProvider,
                ticketsRepository,
                onOverview = { root.nav(Route.Overview(it)) },
                onBack = { root.onBack() }
            )
        )
    }
}
