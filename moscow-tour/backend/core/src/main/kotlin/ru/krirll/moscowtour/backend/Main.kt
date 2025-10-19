package ru.krirll.moscowtour.backend

import io.ktor.server.application.Application
import io.ktor.server.netty.EngineMain
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.context.startKoin
import org.koin.ksp.generated.module
import ru.krirll.moscowtour.backend.presentation.configureJwt
import ru.krirll.moscowtour.backend.presentation.configureRouting
import ru.krirll.moscowtour.backend.presentation.configureSerialization
import ru.krirll.moscowtour.backend.presentation.configureWebSocket
import ru.krirll.moscowtour.shared.newCommonModulesList

val backendModules = newCommonModulesList(true) + BackendModule().module

fun main(args: Array<String>) {
    startKoin { modules(backendModules) }
    EngineMain.main(args)
}

fun Application.module() {
    val koin = object : KoinComponent {}
    configureSerialization()
    configureJwt(koin.get())
    configureWebSocket(koin.get())
    configureRouting(koin.get(), koin.get())
}
