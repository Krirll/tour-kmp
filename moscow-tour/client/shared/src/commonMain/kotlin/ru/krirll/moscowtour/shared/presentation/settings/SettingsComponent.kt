package ru.krirll.moscowtour.shared.presentation.settings

import com.arkivanov.decompose.ComponentContext
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.koin.core.annotation.Factory
import org.koin.core.annotation.Named
import ru.krirll.moscowtour.shared.di.IS_DEBUG_KEY
import ru.krirll.moscowtour.shared.di.factory.DispatcherProvider
import ru.krirll.moscowtour.shared.domain.LogoutUseCase
import ru.krirll.moscowtour.shared.domain.ServerConfigurationRepository
import ru.krirll.moscowtour.shared.presentation.RootComponent
import ru.krirll.moscowtour.shared.presentation.componentScope
import ru.krirll.moscowtour.shared.presentation.nav.Child
import ru.krirll.moscowtour.shared.presentation.nav.ComponentFactory
import ru.krirll.moscowtour.shared.presentation.nav.Route

class SettingsComponent(
    private val context: ComponentContext,
    private val dispatcherProvider: DispatcherProvider,
    serverConfigurationRepository: ServerConfigurationRepository,
    private val logoutUseCase: LogoutUseCase,
    val isDebug: Boolean,
    val doBack: () -> Unit,
    val doAuth: () -> Unit,
    val doRegister: () -> Unit,
    val editPassword: () -> Unit,
) : ComponentContext by context {
    val serverInfo = serverConfigurationRepository.serverConfiguration
        .filterNotNull()
        .map { it.asHttpStr() }

    val tokenInfo = logoutUseCase.token

    fun logout() {
        componentScope.launch(dispatcherProvider.main) {
            logoutUseCase.logout()
        }
    }
}

@Factory(binds = [SettingsComponentFactory::class])
class SettingsComponentFactory(
    private val serverConfigurationRepository: ServerConfigurationRepository,
    private val dispatcherProvider: DispatcherProvider,
    private val logoutUseCase: LogoutUseCase,
    @Named(IS_DEBUG_KEY) private val isDebug: Boolean
) : ComponentFactory<Child.SettingsChild, Route.Settings> {
    override fun create(
        route: Route.Settings,
        child: ComponentContext,
        root: RootComponent
    ): Child.SettingsChild {
        val comp = SettingsComponent(
            child,
            dispatcherProvider,
            serverConfigurationRepository,
            isDebug = isDebug,
            doBack = { root.onBack() },
            doAuth = { root.nav(Route.Settings.Auth()) },
            doRegister = { root.nav(Route.Settings.Register) },
            logoutUseCase = logoutUseCase,
            editPassword = { root.nav(Route.Settings.EditPassword) }
        )
        return Child.SettingsChild(comp)
    }
}

