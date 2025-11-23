package ru.krirll.moscowtour.shared

import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module
import ru.krirll.moscowtour.shared.di.EventsModule
import ru.krirll.moscowtour.shared.di.HttpModule
import ru.krirll.moscowtour.shared.di.SavedModule
import ru.krirll.moscowtour.shared.di.SearchModule
import ru.krirll.moscowtour.shared.di.TicketsModule

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
class SharedModule
