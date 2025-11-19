package ru.krirll.moscowtour.shared.presentation.overview.person

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.lifecycle.coroutines.coroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.koin.core.annotation.Factory
import ru.krirll.domain.DispatcherProvider
import ru.krirll.moscowtour.shared.data.PersonDataValidator
import ru.krirll.moscowtour.shared.domain.model.PersonData
import ru.krirll.moscowtour.shared.domain.model.PersonDataValidationException
import ru.krirll.moscowtour.shared.presentation.RootComponent
import ru.krirll.moscowtour.shared.presentation.base.ScreenState
import ru.krirll.moscowtour.shared.presentation.createErrorHandler
import ru.krirll.moscowtour.shared.presentation.nav.Child
import ru.krirll.moscowtour.shared.presentation.nav.ComponentFactory
import ru.krirll.moscowtour.shared.presentation.nav.Route

class PersonComponent(
    private val context: ComponentContext,
    private val validator: PersonDataValidator,
    private val onContinue: (PersonData) -> Unit,
    val onBack: () -> Unit,
    dispatcherProvider: DispatcherProvider
) : ComponentContext by context {

    private val scope = coroutineScope(SupervisorJob() + dispatcherProvider.main)
    private val handler = createErrorHandler(scope) {
        _state.emit(ScreenState.Error(PersonDataValidationException(it)))
    }

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
        scope.launch(handler) {
            validator.validate(lastName, firstName, middleName, series, number, phone)
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
class PersonFactory(
    private val validator: PersonDataValidator,
    private val dispatcherProvider: DispatcherProvider
) : ComponentFactory<Child.PersonChild, Route.Overview.PersonScreen> {

    override fun create(
        route: Route.Overview.PersonScreen,
        child: ComponentContext,
        root: RootComponent
    ): Child.PersonChild {
        return Child.PersonChild(
            PersonComponent(
                child,
                validator = validator,
                onContinue = { root.nav(Route.Overview.BuyTicket(route.tour, it)) },
                onBack = { root.onBack() },
                dispatcherProvider
            )
        )
    }

}
