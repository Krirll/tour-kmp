package ru.krirll.moscowtour.shared.presentation.loading

import com.arkivanov.decompose.ComponentContext
import kotlinx.coroutines.flow.Flow
import org.koin.core.annotation.Factory
import ru.krirll.moscowtour.shared.presentation.RootComponent
import ru.krirll.moscowtour.shared.presentation.nav.Child
import ru.krirll.moscowtour.shared.presentation.nav.ComponentFactory
import ru.krirll.moscowtour.shared.presentation.nav.Route

class LoadingComponent(
    private val context: ComponentContext,
    val isLoggedIn: Flow<Boolean>,
    val onLoaded: (isLoggedIn: Boolean) -> Unit
) : ComponentContext by context

@Factory(binds = [LoadingComponentFactory::class])
class LoadingComponentFactory : ComponentFactory<Child.LoadingChild, Route.Loading> {
    override fun create(
        route: Route.Loading,
        child: ComponentContext,
        root: RootComponent
    ): Child.LoadingChild {
        return Child.LoadingChild(
            LoadingComponent(
                child,
                root.isLoggedIn,
                onLoaded = { isLoggedIn ->
                    if (!isLoggedIn && route.authRequired) {
                        root.navReplace(Route.Settings.Auth(true, next = route.next))
                    } else {
                        root.navReplace(*route.next.toTypedArray())
                    }
                }
            )
        )
    }
}
