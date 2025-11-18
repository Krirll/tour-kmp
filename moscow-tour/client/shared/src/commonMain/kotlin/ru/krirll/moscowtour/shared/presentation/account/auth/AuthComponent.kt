package ru.krirll.moscowtour.shared.presentation.account.auth

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.lifecycle.coroutines.coroutineScope
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
import ru.krirll.moscowtour.shared.presentation.base.ScreenState
import ru.krirll.moscowtour.shared.presentation.nav.Child
import ru.krirll.moscowtour.shared.presentation.nav.ComponentFactory
import ru.krirll.moscowtour.shared.presentation.nav.Route

class AuthComponent(
    private val context: ComponentContext,
    val doBack: () -> Unit,
    val doRegister: () -> Unit,
    private val authTokenRepository: AuthTokenRepository,
    private val dispatcherProvider: DispatcherProvider,
    private val log: Log
) : ComponentContext by context {

    private val scope = coroutineScope()
    private val _state = MutableSharedFlow<ScreenState>()
    val state = _state.asSharedFlow()

    fun login(login: String, password: String) {
        scope.launch(dispatcherProvider.main) {
            _state.emit(ScreenState.Loading)
            try {
                authTokenRepository.login(LoginInfo(login, password))
                _state.emit(ScreenState.Succeed)
            } catch (e: LoginException) {
                log.d("AuthComponent", "", e)
                _state.emit(ScreenState.Error(e))
            }
        }
    }

    fun finish() {
        doBack()
    }
}

@Factory(binds = [AuthComponentFactory::class])
class AuthComponentFactory(
    private val authTokenRepository: AuthTokenRepository,
    private val dispatcherProvider: DispatcherProvider,
    private val log: Log
) : ComponentFactory<Child.AuthChild, Route.Account.Auth> {

    override fun create(
        route: Route.Account.Auth,
        child: ComponentContext,
        root: RootComponent
    ): Child.AuthChild {
        val comp = AuthComponent(
            child,
            doBack = { root.onBack() },
            doRegister = { root.nav(Route.Account.Register) },
            authTokenRepository,
            dispatcherProvider,
            log
        )
        return Child.AuthChild(comp)
    }
}
