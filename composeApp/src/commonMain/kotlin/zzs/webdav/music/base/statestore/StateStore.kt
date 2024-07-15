package zzs.webdav.music.base.statestore

import kotlinx.coroutines.flow.Flow

interface StateStore<S:Any> {
    val state: S
    val flow: Flow<S>
    fun get(block: (S) -> Unit)
    fun set(stateReducer: S.() -> S)
}