package ru.krirll.moscowtour.shared.domain

interface SyncRepository {
    suspend fun isSearchSynchronized(): Boolean
    suspend fun setSearchSynchronized(isSync: Boolean)
    suspend fun isRecentlyWatchedSynchronized(): Boolean
    suspend fun setRecentlyWatchedSynchronized(isSync: Boolean)
    suspend fun isSavedMovieSynchronized(): Boolean
    suspend fun setSavedMovieSynchronized(isSync: Boolean)
}
