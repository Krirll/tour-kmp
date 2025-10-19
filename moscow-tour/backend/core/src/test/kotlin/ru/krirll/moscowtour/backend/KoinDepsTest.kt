package ru.krirll.moscowtour.backend

import org.junit.Test
import org.koin.test.check.checkKoinModules

class KoinDepsTest {
    @Test
    fun verify() {
        checkKoinModules(
            backendModules,
            appDeclaration = {
                allowOverride(true)
            }
        )
    }
}
