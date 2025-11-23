package ru.krirll.moscowtour.shared.presentation.overview.buy

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.lifecycle.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import org.koin.core.annotation.Factory
import ru.krirll.moscowtour.shared.data.saveFileFromResponse
import ru.krirll.moscowtour.shared.domain.TicketsRepository
import ru.krirll.moscowtour.shared.domain.model.PersonData
import ru.krirll.moscowtour.shared.domain.model.TicketBuyingException
import ru.krirll.moscowtour.shared.domain.model.Tour
import ru.krirll.moscowtour.shared.presentation.RootComponent
import ru.krirll.moscowtour.shared.presentation.base.ScreenState
import ru.krirll.moscowtour.shared.presentation.createErrorHandler
import ru.krirll.moscowtour.shared.presentation.nav.Child
import ru.krirll.moscowtour.shared.presentation.nav.ComponentFactory
import ru.krirll.moscowtour.shared.presentation.nav.Route
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

class BuyComponent(
    private val context: ComponentContext,
    private val ticketsRepository: TicketsRepository,
    val tour: Tour,
    val personData: PersonData,
    val doBack: () -> Unit,
    val navTickets: () -> Unit,
    val navTours: () -> Unit
) : ComponentContext by context {

    private val _state = MutableSharedFlow<ScreenState>()
    val state = _state.asSharedFlow()

    private val scope = coroutineScope()
    private val exceptionHandler = createErrorHandler(scope) {
        _state.emit(ScreenState.Error(TicketBuyingException(it)))
    }

    @OptIn(ExperimentalTime::class)
    fun requestTicket() {
        scope.launch(exceptionHandler) {
            _state.emit(ScreenState.Loading)
            val file = ticketsRepository.createAndDownload(
                tour.id,
                personData,
                Clock.System.now().toEpochMilliseconds()
            )
            saveFileFromResponse(file.byteArray, file.fileName)
            _state.emit(ScreenState.Succeed)
        }
    }

}

@Factory(binds = [BuyFactory::class])
class BuyFactory(
    private val ticketsRepository: TicketsRepository
) : ComponentFactory<Child.BuyChild, Route.Overview.BuyTicket> {

    override fun create(
        route: Route.Overview.BuyTicket,
        child: ComponentContext,
        root: RootComponent
    ): Child.BuyChild {
        return Child.BuyChild(
            BuyComponent(
                child,
                ticketsRepository,
                tour = route.tour,
                personData = route.personData,
                doBack = { root.onBack() },
                navTours = {  },
                navTickets = {  }
            )
        )
    }
}
