package ru.krirll

import kotlinx.serialization.json.Json
import org.koin.core.Koin
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single
import org.koin.core.component.KoinComponent
import ru.krirll.data.LogImpl
import ru.krirll.data.SettingsKeyValueStorage
import ru.krirll.domain.DispatcherProvider
import ru.krirll.domain.KeyValueStorage
import ru.krirll.domain.Log

val koin: Koin get() = object : KoinComponent {}.getKoin()

@Module
@ComponentScan
class CommonModule {

    @Single
    fun provideJson(): Json {
        return Json { ignoreUnknownKeys = true }
    }

    @Single
    fun provideDispatchersProvider(): DispatcherProvider {
        return object : DispatcherProvider {}
    }

    @Single
    fun provideLog(): Log {
        return LogImpl()
    }

    @Single
    fun provideSettings(
        dispatcherProvider: DispatcherProvider, log: Log
    ): KeyValueStorage {
        return SettingsKeyValueStorage(dispatcherProvider, log, "app_settings")
    }
}
