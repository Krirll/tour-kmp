package ru.krirll.moscowtour.shared.presentation.nav

import com.arkivanov.decompose.ComponentContext
import ru.krirll.moscowtour.shared.presentation.RootComponent

interface ComponentFactory<T : Child, R : Route> {
    fun create(route: R, child: ComponentContext, root: RootComponent): T
}
