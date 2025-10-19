package ru.krirll.moscowtour.shared.di

import org.koin.core.annotation.Factory
import org.koin.core.annotation.Module
import org.koin.core.annotation.Named
import ru.krirll.http.domain.TokenStorage
import ru.krirll.moscowtour.shared.data.search.RemoteSearchRepository
import ru.krirll.moscowtour.shared.data.search.SqlSearchRepository
import ru.krirll.moscowtour.shared.data.search.SyncClientServerSearchRepository
import ru.krirll.moscowtour.shared.domain.SearchRepository
import ru.krirll.moscowtour.shared.domain.SyncRepository

@Module
class SearchModule {

    @Named(DbType.LOCAL)
    @Factory
    fun provideLocalSearchRepo(impl: SqlSearchRepository): SearchRepository {
        return impl
    }

    @Named(DbType.REMOTE)
    @Factory
    fun provideRemoteSearchRepo(impl: RemoteSearchRepository): SearchRepository {
        return impl
    }

    @Factory
    fun provideSearchRepo(
        @Named(DbType.LOCAL) local: SearchRepository,
        @Named(DbType.REMOTE) remote: SearchRepository,
        syncRepository: SyncRepository,
        authTokenCache: TokenStorage
    ) : SearchRepository {
        return SyncClientServerSearchRepository(remote, local, syncRepository, authTokenCache)
    }
}
