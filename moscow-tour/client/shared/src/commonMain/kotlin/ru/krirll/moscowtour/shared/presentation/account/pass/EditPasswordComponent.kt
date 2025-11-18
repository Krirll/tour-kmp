package ru.krirll.moscowtour.shared.presentation.account.pass

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.lifecycle.coroutines.coroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.koin.core.annotation.Factory
import ru.krirll.domain.DispatcherProvider
import ru.krirll.moscowtour.shared.domain.AuthTokenRepository
import ru.krirll.moscowtour.shared.domain.ChangePasswordRequest
import ru.krirll.moscowtour.shared.domain.model.LoginException
import ru.krirll.moscowtour.shared.domain.model.PasswordsNotEqualsException
import ru.krirll.moscowtour.shared.presentation.RootComponent
import ru.krirll.moscowtour.shared.presentation.base.ScreenState
import ru.krirll.moscowtour.shared.presentation.createErrorHandler
import ru.krirll.moscowtour.shared.presentation.nav.Child
import ru.krirll.moscowtour.shared.presentation.nav.ComponentFactory
import ru.krirll.moscowtour.shared.presentation.nav.Route

class EditPasswordComponent(
    private val context: ComponentContext,
    private val authTokenRepository: AuthTokenRepository,
    dispatcherProvider: DispatcherProvider,
    val onBack: () -> Unit
) : ComponentContext by context {

    private val scope = coroutineScope(SupervisorJob() + dispatcherProvider.main)
    private val errorHandler = createErrorHandler(scope) {
        _state.emit(ScreenState.Error(LoginException(it)))
    }

    private val _state = MutableStateFlow<ScreenState>(ScreenState.Idle)
    val state = _state.asStateFlow()

    fun resetState() {
        _state.tryEmit(ScreenState.Idle)
    }

    fun changePassword(old: String, new: String, repeatNew: String) {
        scope.launch(errorHandler) {
            _state.emit(ScreenState.Loading)
            if (new != repeatNew) {
                throw PasswordsNotEqualsException()
            }
            authTokenRepository.changePassword(
                ChangePasswordRequest(old, new)
            )
            _state.emit(ScreenState.Succeed)
        }
    }
}

@Factory(binds = [EditPasswordComponentFactory::class])
class EditPasswordComponentFactory(
    private val authTokenRepository: AuthTokenRepository,
    private val dispatcherProvider: DispatcherProvider
) : ComponentFactory<Child.EditPasswordChild, Route.Account.EditPassword> {

    override fun create(
        route: Route.Account.EditPassword,
        child: ComponentContext,
        root: RootComponent
    ): Child.EditPasswordChild {
        val component = EditPasswordComponent(
            child,
            authTokenRepository,
            onBack = { root.onBack() },
            dispatcherProvider = dispatcherProvider
        )
        return Child.EditPasswordChild(component)
    }
}


