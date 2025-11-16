package ru.krirll.moscowtour.shared.presentation.account

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.lifecycle.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import org.koin.core.annotation.Factory
import ru.krirll.moscowtour.shared.di.factory.DispatcherProvider
import ru.krirll.moscowtour.shared.domain.LogoutUseCase
import ru.krirll.moscowtour.shared.presentation.RootComponent
import ru.krirll.moscowtour.shared.presentation.account.auth.AuthState
import ru.krirll.moscowtour.shared.presentation.nav.Child
import ru.krirll.moscowtour.shared.presentation.nav.ComponentFactory
import ru.krirll.moscowtour.shared.presentation.nav.Route

class AccountComponent(
    private val context: ComponentContext,
    private val dispatcherProvider: DispatcherProvider,
    private val logoutUseCase: LogoutUseCase,
    val doBack: () -> Unit,
    val doAuth: () -> Unit,
    val doRegister: () -> Unit,
    val editPassword: () -> Unit,
    val tickets: () -> Unit
) : ComponentContext by context {

    private val _state = MutableSharedFlow<AuthState>()
    val state = _state.asSharedFlow()

    private val scope = coroutineScope()

    val tokenInfo = logoutUseCase.token

    fun logout() {
        logout(false)
    }

    fun delete() {
        logout(true)
    }

    private fun logout(withDelete: Boolean) {
        scope.launch(dispatcherProvider.main) {
            _state.emit(AuthState.Loading)
            logoutUseCase.logout(withDelete)
            _state.emit(AuthState.Succeed)
        }
    }
}

@Factory(binds = [AccountComponentFactory::class])
class AccountComponentFactory(
    private val dispatcherProvider: DispatcherProvider,
    private val logoutUseCase: LogoutUseCase
) : ComponentFactory<Child.AccountChild, Route.Account> {

    override fun create(
        route: Route.Account,
        child: ComponentContext,
        root: RootComponent
    ): Child.AccountChild {
        val comp = AccountComponent(
            child,
            dispatcherProvider,
            doBack = { root.onBack() },
            doAuth = { root.nav(Route.Account.Auth()) },
            doRegister = { root.nav(Route.Account.Register) },
            logoutUseCase = logoutUseCase,
            editPassword = { root.nav(Route.Account.EditPassword) },
            tickets = { root.nav(Route.Account.Tickets) }
        )
        return Child.AccountChild(comp)
    }
}

