package ru.krirll.moscowtour.shared.presentation.settings.serv

import com.arkivanov.decompose.ComponentContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.koin.core.annotation.Factory
import ru.krirll.moscowtour.shared.di.factory.DispatcherProvider
import ru.krirll.moscowtour.shared.domain.ServerConfiguration
import ru.krirll.moscowtour.shared.domain.ServerConfigurationRepository
import ru.krirll.moscowtour.shared.presentation.RootComponent
import ru.krirll.moscowtour.shared.presentation.componentScope
import ru.krirll.moscowtour.shared.presentation.nav.Child
import ru.krirll.moscowtour.shared.presentation.nav.ComponentFactory
import ru.krirll.moscowtour.shared.presentation.nav.Route

class EditServComponent(
    private val context: ComponentContext,
    private val serverConfigurationRepository: ServerConfigurationRepository,
    private val dispatcherProvider: DispatcherProvider,
    val doBack: () -> Unit
) : ComponentContext by context {
    val serverUrl = serverConfigurationRepository.serverConfiguration
        .filterNotNull()
        .map { it.asHttpStr() }
        .onEach { onUrlChanged(it) }
    private val _state = MutableStateFlow<EditServState>(EditServState.Idle)
    val state = _state.asStateFlow()
    private var cached: String? = null

    fun onUrlChanged(cached: String) {
        this.cached = cached
        componentScope.launch(dispatcherProvider.main) { parseConfiguration(cached) }
    }

    private suspend fun parseConfiguration(url: String?): ServerConfiguration? {
        return if (url == null || url.trim().isEmpty()) {
            _state.emit(EditServState.Error(EditServError.EMPTY_URL))
            null
        } else {
            ServerConfiguration(url)
        }
    }

    fun done() {
        componentScope.launch(dispatcherProvider.main) {
            parseConfiguration(cached)?.let {
                serverConfigurationRepository.setServerConfiguration(it)
                _state.emit(EditServState.Succeed)
            }
        }
    }
}

@Factory(binds = [EditServComponentFactory::class])
class EditServComponentFactory(
    private val serverConfigurationRepository: ServerConfigurationRepository,
    private val dispatcherProvider: DispatcherProvider
) : ComponentFactory<Child.EditServerAddrChild, Route.Settings.EditServ> {
    override fun create(
        route: Route.Settings.EditServ,
        child: ComponentContext,
        root: RootComponent
    ): Child.EditServerAddrChild {
        val comp = EditServComponent(
            child,
            serverConfigurationRepository,
            dispatcherProvider,
            doBack = { root.onBack() }
        )
        return Child.EditServerAddrChild(comp)
    }
}

sealed interface EditServState {
    data object Idle : EditServState
    data class Error(val error: EditServError) : EditServState
    data object Succeed : EditServState
}

enum class EditServError {
    EMPTY_URL
}
