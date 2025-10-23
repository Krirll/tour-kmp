package ru.krirll.moscowtour.shared.domain

interface SyncRepository {
    suspend fun isSearchSynchronized(): Boolean
    suspend fun setSearchSynchronized(isSync: Boolean)
    suspend fun isSavedToursSynchronized(): Boolean
    suspend fun setSavedToursSynchronized(isSync: Boolean)
}
