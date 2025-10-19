package ru.krirll.moscowtour.shared.data

import kotlinx.coroutines.flow.first
import org.koin.core.annotation.Factory
import ru.krirll.domain.KeyValueStorage
import ru.krirll.moscowtour.shared.domain.SyncRepository

@Factory
class SyncRepositoryImpl(
    private val storage: KeyValueStorage
) : SyncRepository {

    override suspend fun isSearchSynchronized(): Boolean = query(SEARCH_KEY)
    override suspend fun isRecentlyWatchedSynchronized(): Boolean = query(RECENT_KEY)
    override suspend fun isSavedMovieSynchronized(): Boolean = query(SAVED_KEY)

    private suspend fun query(boolPrefKey: String): Boolean {
        return storage.getBoolean(boolPrefKey).first() ?: false
    }

    override suspend fun setSearchSynchronized(isSync: Boolean) {
        update(SEARCH_KEY, isSync)
    }

    override suspend fun setRecentlyWatchedSynchronized(isSync: Boolean) {
        update(RECENT_KEY, isSync)
    }

    override suspend fun setSavedMovieSynchronized(isSync: Boolean) {
        update(SAVED_KEY, isSync)
    }

    private suspend fun update(boolPrefKey: String, value: Boolean) {
        storage.putBoolean(boolPrefKey, value)
    }

    companion object {
        const val SEARCH_KEY = "search"
        const val RECENT_KEY = "recent"
        const val SAVED_KEY = "saved"
    }
}
