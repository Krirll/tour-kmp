package ru.krirll.moscowtour.backend.data.auth

import org.koin.core.annotation.Factory
import ru.krirll.backend.data.StringFetcher
import ru.krirll.backend.data.StringResource
import ru.krirll.backend.domain.LoginVerifier
import ru.krirll.backend.domain.VerifyTokenException
import ru.krirll.moscowtour.backend.AppDatabase

@Factory
class BackendLoginVerifier(
    private val db: AppDatabase,
    private val stringFetcher: StringFetcher
) : LoginVerifier {
    override suspend fun verify(login: String) {
        db.accountsQueries.selectAccountByLogin(login)
            .executeAsOneOrNull()
            ?: throw VerifyTokenException(stringFetcher.get(StringResource.UNKNOWN_USER))
    }
}
