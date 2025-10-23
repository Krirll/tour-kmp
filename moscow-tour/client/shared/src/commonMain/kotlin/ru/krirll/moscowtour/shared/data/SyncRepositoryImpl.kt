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
    override suspend fun isSavedToursSynchronized(): Boolean = query(SAVED_KEY)

    override suspend fun setSearchSynchronized(isSync: Boolean) {
        update(SEARCH_KEY, isSync)
    }

    override suspend fun setSavedToursSynchronized(isSync: Boolean) {
        update(SAVED_KEY, isSync)
    }

    private suspend fun query(boolPrefKey: String): Boolean {
        return storage.getBoolean(boolPrefKey).first() ?: false
    }

    private suspend fun update(boolPrefKey: String, value: Boolean) {
        storage.putBoolean(boolPrefKey, value)
    }

    private companion object {
        const val SEARCH_KEY = "search"
        const val SAVED_KEY = "saved"
    }
}
