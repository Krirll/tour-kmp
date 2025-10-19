package ru.krirll.moscowtour.shared.di

import org.koin.core.annotation.Factory
import org.koin.core.annotation.Module
import org.koin.core.annotation.Named
import ru.krirll.http.domain.TokenStorage
import ru.krirll.moscowtour.shared.data.saved.RemoteSavedToursRepository
import ru.krirll.moscowtour.shared.data.saved.SqlSavedToursRepository
import ru.krirll.moscowtour.shared.data.saved.SyncClientServerSavedToursRepository
import ru.krirll.moscowtour.shared.domain.SavedToursRepository
import ru.krirll.moscowtour.shared.domain.SyncRepository

@Module
class SavedModule {

    @Factory
    @Named(DbType.LOCAL)
    fun provideSavedLocalRepository(impl: SqlSavedToursRepository): SavedToursRepository {
        return impl
    }

    @Factory
    @Named(DbType.REMOTE)
    fun provideSavedRemoteRepository(impl: RemoteSavedToursRepository): SavedToursRepository {
        return impl
    }

    @Factory
    fun provideSavedMovieRepository(
        @Named(DbType.LOCAL) local: SavedToursRepository,
        @Named(DbType.REMOTE) remote: SavedToursRepository,
        syncRepository: SyncRepository,
        authTokenCache: TokenStorage
    ): SavedToursRepository {
        return SyncClientServerSavedToursRepository(
            remote,
            local,
            syncRepository,
            authTokenCache
        )
    }
}
