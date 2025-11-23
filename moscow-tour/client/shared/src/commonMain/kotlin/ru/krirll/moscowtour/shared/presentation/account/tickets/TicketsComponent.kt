package ru.krirll.moscowtour.shared.presentation.account.tickets

import com.arkivanov.decompose.ComponentContext
import org.koin.core.annotation.Factory
import ru.krirll.moscowtour.shared.di.factory.DispatcherProvider
import ru.krirll.moscowtour.shared.domain.TicketsRepository
import ru.krirll.moscowtour.shared.presentation.RootComponent
import ru.krirll.moscowtour.shared.presentation.nav.Child
import ru.krirll.moscowtour.shared.presentation.nav.ComponentFactory
import ru.krirll.moscowtour.shared.presentation.nav.Route

class TicketsComponent(
    private val context: ComponentContext,
) : ComponentContext by context {

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
                child
            )
        )
    }
}
