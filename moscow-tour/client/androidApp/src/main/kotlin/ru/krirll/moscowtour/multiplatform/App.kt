package ru.krirll.moscowtour.multiplatform

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.dsl.bind
import org.koin.dsl.module
import ru.krirll.moscowtour.shared.di.newKoinModules
import ru.krirll.moscowtour.shared.presentation.UiConfiguration

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@App)
            modules(newKoinModules(BuildConfig.DEBUG))
            allowOverride(true)
        }
    }
}
