package ru.krirll.moscowtour.shared

import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module
import org.koin.core.annotation.Singleton
import org.koin.core.qualifier.named
import org.koin.dsl.module
import org.koin.ksp.generated.module
import ru.krirll.CommonModule
import ru.krirll.http.HttpModule
import ru.krirll.moscowtour.shared.di.factory.FileProvider

const val IS_SERVER = "is_server"

@Module(includes = [HttpModule::class, CommonModule::class])
@ComponentScan
class MoscowTourCommon {

    @Singleton
    fun provideFileProvider(): FileProvider {
        return FileProvider()
    }
}

fun newCommonModulesList(isServer: Boolean) = listOf(
    MoscowTourCommon().module,
    module {
        single(named(IS_SERVER)) { isServer }
    }
)
