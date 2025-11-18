package ru.krirll.moscowtour.shared.presentation.overview.person

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.lifecycle.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.koin.core.annotation.Factory
import ru.krirll.moscowtour.shared.domain.model.PersonData
import ru.krirll.moscowtour.shared.presentation.RootComponent
import ru.krirll.moscowtour.shared.presentation.base.ScreenState
import ru.krirll.moscowtour.shared.presentation.nav.Child
import ru.krirll.moscowtour.shared.presentation.nav.ComponentFactory
import ru.krirll.moscowtour.shared.presentation.nav.Route

class PersonComponent(
    private val context: ComponentContext,
    private val onContinue: (PersonData) -> Unit,
    val onBack: () -> Unit
) : ComponentContext by context {

    private val scope = coroutineScope()
    private val _state = MutableStateFlow<ScreenState>(ScreenState.Idle)
    val state = _state.asStateFlow()

    private var lastValidatedPersonData: PersonData? = null

    fun validateData(
        lastName: String,
        firstName: String,
        middleName: String,
        series: String,
        number: String,
        phone: String
    ) {
        scope.launch {
            //todo validation в отдельном классе
            lastValidatedPersonData = PersonData(
                lastName = lastName,
                firstName = firstName,
                middleName = middleName,
                passportSeries = series.toInt(),
                passportNumber = number.toInt(),
                phone = phone
            )
            _state.emit(ScreenState.Succeed)
        }
    }

    fun goNext() {
        val data = requireNotNull(lastValidatedPersonData)
        onContinue(data)
        resetState()
    }

    fun resetState() {
        _state.tryEmit(ScreenState.Idle)
    }
}

@Factory(binds = [PersonFactory::class])
class PersonFactory : ComponentFactory<Child.PersonChild, Route.Overview.PersonScreen> {

    override fun create(
        route: Route.Overview.PersonScreen,
        child: ComponentContext,
        root: RootComponent
    ): Child.PersonChild {
        return Child.PersonChild(
            PersonComponent(
                child,
                onContinue = { root.nav(Route.Overview.BuyTicket(route.tour, it)) },
                onBack = { root.onBack() }
            )
        )
    }

}
