package ru.krirll.moscowtour

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.ComposeViewport
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.router.webhistory.withWebHistory
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.arkivanov.essenty.lifecycle.doOnResume
import com.arkivanov.essenty.statekeeper.StateKeeper
import org.koin.core.context.startKoin
import ru.krirll.koin
import ru.krirll.moscowtour.shared.di.newKoinModules
import ru.krirll.moscowtour.shared.presentation.Nav
import ru.krirll.moscowtour.shared.presentation.RootComponent
import ru.krirll.moscowtour.shared.presentation.RootFactory
import ru.krirll.moscowtour.shared.presentation.nav.Route

fun main() {
    try {
        mainInternal()
    } catch (e: Throwable) {
        println(e.stackTraceToString())
        throw e
    }
}

@OptIn(ExperimentalDecomposeApi::class, ExperimentalComposeUiApi::class)
fun mainInternal() {
    val lifecycle = LifecycleRegistry()
    startKoin { modules(newKoinModules(false)) }

    var rootRef: RootComponent? = null

    val root: RootComponent = if (rootRef == null) {
        val ctx = DefaultComponentContext(
            lifecycle = lifecycle
        )
        val initRoute: Route = Route.default

        rootRef = koin.get<RootFactory>().create(
            context = ctx, initStack = initRoute
        )
        rootRef
    } else {
        rootRef
    }

    hookPageVisibility(lifecycle)
    lifecycle.doOnResume { hideElementById("loader") }

    ComposeViewport(content = {
        val r = remember { root }

        LaunchedEffect(null) {
            r.onFinish.collect { r.navReplace(Route.Loading()) }
        }

        val isLoggedIn by r.isLoggedIn.collectAsState(null)
        LaunchedEffect(isLoggedIn) {
            if (isLoggedIn == false) {
                r.navReplace(Route.Loading())
            }
        }

        Box(Modifier.fillMaxSize().padding(WindowInsets.safeDrawing.asPaddingValues())) {
            Nav(r)
        }
    })
}
