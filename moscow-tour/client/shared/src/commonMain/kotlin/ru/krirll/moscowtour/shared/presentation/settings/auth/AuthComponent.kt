package ru.krirll.moscowtour.shared.presentation.settings.auth

import com.arkivanov.decompose.ComponentContext
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import org.koin.core.annotation.Factory
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

class AuthComponent(
    val isAuthRequired: Boolean,
    private val context: ComponentContext,
    val doBack: () -> Unit,
    val doRegister: () -> Unit,
    val doMainMenu: () -> Unit,
    private val authTokenRepository: AuthTokenRepository,
    private val dispatcherProvider: DispatcherProvider,
    private val log: Log
) : ComponentContext by context {
    private val _state = MutableSharedFlow<AuthState>()
    val state = _state.asSharedFlow()

    fun login(login: String, password: String) {
        componentScope.launch(dispatcherProvider.main) {
            _state.emit(AuthState.Loading)
            try {
                authTokenRepository.login(LoginInfo(login, password))
                _state.emit(AuthState.Succeed)
            } catch (e: LoginException) {
                log.d("AuthComponent", "", e)
                _state.emit(AuthState.Error(e))
            }
        }
    }

    fun finish() {
        if (isAuthRequired) {
            doMainMenu()
        } else {
            doBack()
        }
    }
}

@Factory(binds = [AuthComponentFactory::class])
class AuthComponentFactory(
    private val authTokenRepository: AuthTokenRepository,
    private val dispatcherProvider: DispatcherProvider,
    private val log: Log
) : ComponentFactory<Child.AuthChild, Route.Settings.Auth> {
    override fun create(
        route: Route.Settings.Auth, child: ComponentContext, root: RootComponent
    ): Child.AuthChild {
        val comp = AuthComponent(
            route.required,
            child,
            doBack = { root.onBack() },
            doRegister = { root.nav(Route.Settings.Register) },
            doMainMenu = { root.navReplace(*route.next.toTypedArray()) },
            authTokenRepository,
            dispatcherProvider,
            log
        )
        return Child.AuthChild(comp)
    }
}

sealed interface AuthState {
    data object Idle : AuthState
    data object Loading : AuthState
    data class Error(val e: LoginException) : AuthState
    data object Succeed : AuthState
}
