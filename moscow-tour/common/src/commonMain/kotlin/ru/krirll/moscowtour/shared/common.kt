package ru.krirll.moscowtour.shared

import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module
import org.koin.core.qualifier.named
import org.koin.dsl.module
import org.koin.ksp.generated.module
import ru.krirll.CommonModule
import ru.krirll.http.HttpModule

const val IS_SERVER = "is_server"

@Module(includes = [HttpModule::class, CommonModule::class])
@ComponentScan
class MoscowTourCommon

fun newCommonModulesList(isServer: Boolean) = listOf(
    MoscowTourCommon().module,
    module {
        single(named(IS_SERVER)) { isServer }
    }
)
