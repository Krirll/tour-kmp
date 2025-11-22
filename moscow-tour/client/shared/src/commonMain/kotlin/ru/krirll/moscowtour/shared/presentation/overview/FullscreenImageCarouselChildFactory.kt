package ru.krirll.moscowtour.shared.presentation.overview

import com.arkivanov.decompose.ComponentContext
import org.koin.core.annotation.Factory
import ru.krirll.moscowtour.shared.presentation.RootComponent
import ru.krirll.moscowtour.shared.presentation.nav.Child
import ru.krirll.moscowtour.shared.presentation.nav.ComponentFactory
import ru.krirll.moscowtour.shared.presentation.nav.Route

@Factory(binds = [FullscreenImageCarouselChildFactory::class])
class FullscreenImageCarouselChildFactory :
    ComponentFactory<Child.FullscreenImages, Route.Overview.FullscreenImages> {

    override fun create(
        route: Route.Overview.FullscreenImages,
        child: ComponentContext,
        root: RootComponent
    ): Child.FullscreenImages {
        return Child.FullscreenImages()
    }
}
