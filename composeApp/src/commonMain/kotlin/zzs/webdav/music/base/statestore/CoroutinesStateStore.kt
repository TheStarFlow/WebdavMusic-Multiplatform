package zzs.webdav.music.base.statestore

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.selects.select
import zzs.webdav.music.base.IState
import java.util.concurrent.Executors
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

class CoroutinesStateStore<S : IState>(
    val initialState: S,
    private val scope: CoroutineScope,
    private val contextOverride: CoroutineContext = EmptyCoroutineContext
) : StateStore<S> {


    private val setStateChannel = Channel<S.() -> S>(capacity = Channel.UNLIMITED)
    private val withStateChannel = Channel<(S) -> Unit>(capacity = Channel.UNLIMITED)


    init {
        setupTriggerFlushQueues(scope)
    }


    private val stateSharedFlow = MutableSharedFlow<S>(
        replay = 1,
        extraBufferCapacity = 63,
        onBufferOverflow = BufferOverflow.SUSPEND,
    ).apply { tryEmit(initialState) }


    override var state: S = initialState


    override val flow: Flow<S> = stateSharedFlow.asSharedFlow()
    override fun set(stateReducer: S.() -> S) {
        setStateChannel.trySend(stateReducer)
    }

    override fun get(block: (S) -> Unit) {
        withStateChannel.trySend(block)
    }

    private fun setupTriggerFlushQueues(scope: CoroutineScope) {

        scope.launch(flushDispatcher + contextOverride) {
            while (isActive) {
                flushQueuesOnce()
            }
        }
    }


    private suspend fun flushQueuesOnce() {
        select<Unit> {
            setStateChannel.onReceive { reducer ->
                val newState = state.reducer()
                if (newState != state) {
                    state = newState
                    stateSharedFlow.emit(newState)
                }
            }
            withStateChannel.onReceive { block ->
                block(state)
            }
        }
    }


    companion object {
        private val flushDispatcher = Executors.newCachedThreadPool().asCoroutineDispatcher()

    }


}