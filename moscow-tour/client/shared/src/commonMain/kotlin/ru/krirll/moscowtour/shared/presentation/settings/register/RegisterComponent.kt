package ru.krirll.moscowtour.shared.presentation.settings.register

import com.arkivanov.decompose.ComponentContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.koin.core.annotation.Factory
import org.koin.core.component.KoinComponent
import ru.krirll.domain.Log
import ru.krirll.moscowtour.shared.di.factory.DispatcherProvider
import ru.krirll.moscowtour.shared.domain.AuthTokenRepository
import ru.krirll.moscowtour.shared.domain.model.LoginException
import ru.krirll.moscowtour.shared.domain.model.LoginInfo
import ru.krirll.moscowtour.shared.presentation.RootComponent
import ru.krirll.moscowtour.shared.presentation.componentScope
import ru.krirll.moscowtour.shared.presentation.nav.Child
import ru.krirll.moscowtour.shared.presentation.nav.ComponentFactory
import ru.krirll.moscowtour.shared.presentation.nav.Route
import ru.krirll.moscowtour.shared.presentation.settings.auth.AuthState

class RegisterComponent(
    private val context: ComponentContext,
    val doBack: () -> Unit,
    private val authTokenRepository: AuthTokenRepository,
    private val dispatcherProvider: DispatcherProvider,
    private val log: Log,
) : ComponentContext by context, KoinComponent {
    private val _state = MutableStateFlow<AuthState>(AuthState.Idle)
    val state = _state.asStateFlow()

    fun register(login: String, password: String) {
        componentScope.launch(dispatcherProvider.main) {
            _state.emit(AuthState.Loading)
            try {
                authTokenRepository.register(LoginInfo(login, password))
                _state.emit(AuthState.Succeed)
            } catch (e: LoginException) {
                log.e("RegisterComponent", e)
                _state.value = AuthState.Error(e)
            }
        }
    }
}

@Factory(binds = [RegisterComponentFactory::class])
class RegisterComponentFactory(
    private val authTokenRepository: AuthTokenRepository,
    private val dispatcherProvider: DispatcherProvider,
    private val log: Log
) : ComponentFactory<Child.RegisterChild, Route.Settings.Register> {
    override fun create(
        route: Route.Settings.Register,
        child: ComponentContext,
        root: RootComponent
    ): Child.RegisterChild {
        val comp = RegisterComponent(
            child,
            doBack = { root.onBack() },
            authTokenRepository,
            dispatcherProvider,
            log
        )
        return Child.RegisterChild(comp)
    }
}

