package ru.krirll.moscowtour.shared.di

import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.module
import org.koin.ksp.generated.module
import ru.krirll.moscowtour.shared.SharedModule
import ru.krirll.moscowtour.shared.newCommonModulesList

const val IS_DEBUG_KEY = "is_debug_flag"

val koin get() = ru.krirll.koin

fun newKoinModules(isDebug: Boolean): List<Module> {
    return listOf(
        module {
            single(named(IS_DEBUG_KEY)) { isDebug }
        },
        SharedModule().module,
    ) + newCommonModulesList(false)
}
