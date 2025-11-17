package ru.krirll.moscowtour.shared.presentation.nav

import ru.krirll.moscowtour.shared.presentation.list.ToursScreenComponent
import ru.krirll.moscowtour.shared.presentation.loading.LoadingComponent
import ru.krirll.moscowtour.shared.presentation.overview.OverviewComponent
import ru.krirll.moscowtour.shared.presentation.saved.SavedToursScreenComponent
import ru.krirll.moscowtour.shared.presentation.search.SearchScreenComponent
import ru.krirll.moscowtour.shared.presentation.account.AccountComponent
import ru.krirll.moscowtour.shared.presentation.account.auth.AuthComponent
import ru.krirll.moscowtour.shared.presentation.account.pass.EditPasswordComponent
import ru.krirll.moscowtour.shared.presentation.account.register.RegisterComponent
import ru.krirll.moscowtour.shared.presentation.account.tickets.TicketsComponent
import ru.krirll.moscowtour.shared.presentation.overview.buy.BuyComponent
import ru.krirll.moscowtour.shared.presentation.overview.person.PersonComponent

sealed class Child : ru.krirll.ui.nav.Child {
    class ToursChild(val component: ToursScreenComponent) : Child()
    class SearchChild(val component: SearchScreenComponent) : Child()
    class OverviewChild(val component: OverviewComponent) : Child()
    class PersonChild(val component: PersonComponent) : Child()
    class BuyChild(val component: BuyComponent) : Child()
    class AccountChild(val component: AccountComponent) : Child()
    class AuthChild(val component: AuthComponent) : Child()
    class RegisterChild(val component: RegisterComponent) : Child()
    class SavedToursChild(val component: SavedToursScreenComponent) : Child()
    class EditPasswordChild(val component: EditPasswordComponent) : Child()
    class TicketsChild(val component: TicketsComponent) : Child()
    class LoadingChild(val component: LoadingComponent) : Child()
}
