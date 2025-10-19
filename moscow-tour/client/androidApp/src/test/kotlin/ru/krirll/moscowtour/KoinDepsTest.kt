package ru.krirll.moscowtour

import android.app.Application
import android.content.Context
import io.mockk.mockk
import org.junit.Test
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.core.qualifier.named
import org.koin.dsl.binds
import org.koin.dsl.module
import org.koin.test.check.checkKoinModules
import ru.krirll.http.data.USE_HTTP_CUSTOM_CERTS
import ru.krirll.moscowtour.shared.di.koin
import ru.krirll.moscowtour.shared.di.newKoinModules
import ru.krirll.moscowtour.shared.presentation.nav.Child
import ru.krirll.moscowtour.shared.presentation.nav.Route
import ru.krirll.moscowtour.shared.presentation.provideFactory

class KoinDepsTest {
    private val fakeContext = mockk<Application>(relaxed = true)

    private val testModule = module {
        single(named(USE_HTTP_CUSTOM_CERTS)) { false }
        single { fakeContext } binds(arrayOf(Context::class, Application::class))
    }

    @Test
    fun verify() {
        checkKoinModules(newKoinModules(true), appDeclaration = {
            androidContext(fakeContext)
            modules(testModule)
        })
    }

    @Test
    fun checkComponents() {
        startKoin {
            modules(newKoinModules(true))
            androidContext(fakeContext)
            modules(testModule)
        }
        listOf<Route>(
            Route.Settings.Auth(false),
            Route.Settings.EditServ,
            Route.Overview.Episode(1, 1),
            Route.Overview(1),
            Route.Settings.Register,
            Route.Saved,
            Route.SearchVideos,
            Route.Overview.Season(1),
            Route.Settings,
            Route.Videos(null),
            Route.Settings.EditPassword
        ).forEach { it.check() }
    }

    private fun Route.check() {
        provideFactory<Child, Route>(koin)

        when (this) {
            is Route.Loading,
            is Route.Overview,
            is Route.Overview.Episode,
            is Route.Overview.Season,
            Route.Saved,
            Route.SearchVideos,
            Route.Settings,
            is Route.Settings.Auth,
            Route.Settings.EditPassword,
            Route.Settings.EditServ,
            Route.Settings.Register,
            is Route.Videos -> {}
        }
    }
}
