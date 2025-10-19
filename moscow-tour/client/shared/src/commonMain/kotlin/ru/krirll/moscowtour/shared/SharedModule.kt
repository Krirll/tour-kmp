package ru.krirll.moscowtour.shared

import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Factory
import org.koin.core.annotation.Module
import ru.krirll.moscowtour.shared.di.EventsModule
import ru.krirll.moscowtour.shared.di.HttpModule
import ru.krirll.moscowtour.shared.di.SavedModule
import ru.krirll.moscowtour.shared.di.SearchModule
import ru.krirll.moscowtour.shared.di.TicketsModule
import ru.krirll.moscowtour.shared.presentation.ShareManager
import ru.krirll.moscowtour.shared.presentation.VideoLauncher

@Module(
    includes = [
        EventsModule::class,
        HttpModule::class,
        SearchModule::class,
        SavedModule::class,
        TicketsModule::class
    ]
)
@ComponentScan
class SharedModule {

    @Factory
    fun provideShareManager(): ShareManager = ShareManager()

    @Factory
    fun provideVideoLauncher(): VideoLauncher = VideoLauncher()
}
