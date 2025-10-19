package ru.krirll.moscowtour.shared.presentation.settings.pass

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
import ru.krirll.moscowtour.shared.presentation.RootComponent
import ru.krirll.moscowtour.shared.presentation.createErrorHandler
import ru.krirll.moscowtour.shared.presentation.nav.Child
import ru.krirll.moscowtour.shared.presentation.nav.ComponentFactory
import ru.krirll.moscowtour.shared.presentation.nav.Route
import ru.krirll.moscowtour.shared.presentation.settings.auth.AuthState

class EditPasswordComponent(
    private val context: ComponentContext,
    private val authTokenRepository: AuthTokenRepository,
    private val dispatcherProvider: DispatcherProvider,
    val onBack: () -> Unit
) : ComponentContext by context {
    private val scope = coroutineScope(SupervisorJob() + dispatcherProvider.main)
    private val _state = MutableStateFlow<AuthState>(AuthState.Idle)
    val state = _state.asStateFlow()
    private val errorHandler = createErrorHandler {
        _state.emit(AuthState.Error(LoginException(it)))
    }

    fun resetState() {
        _state.tryEmit(AuthState.Idle)
    }

    fun changePassword(old: String, new: String) {
        scope.launch(errorHandler) {
            _state.emit(AuthState.Loading)
            authTokenRepository.changePassword(
                ChangePasswordRequest(old, new)
            )
            _state.emit(AuthState.Succeed)
        }
    }
}

@Factory(binds = [EditPasswordComponentFactory::class])
class EditPasswordComponentFactory(
    private val authTokenRepository: AuthTokenRepository,
    private val dispatcherProvider: DispatcherProvider
) : ComponentFactory<Child.EditPasswordChild, Route.Settings.EditPassword> {
    override fun create(
        route: Route.Settings.EditPassword,
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


