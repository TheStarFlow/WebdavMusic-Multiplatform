package zzs.webdav.music.base

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import zzs.webdav.music.base.statestore.CoroutinesStateStore
import java.lang.reflect.ParameterizedType
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.cancellation.CancellationException
import kotlin.reflect.KProperty1

abstract class BaseViewModel<S : IState, I : IIntent> : ViewModel() {

    protected val viewModelScope: CoroutineScope =
        CoroutineScope(SupervisorJob() + Dispatchers.Default)


    private val stateStore by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
        CoroutinesStateStore(initialState = initUiState(), viewModelScope)
    }


    val uiStateFlow: Flow<S> = stateStore.flow


    private val _uiIntentFlow: Channel<I> = Channel()
    private val uiIntentFlow: Flow<I> = _uiIntentFlow.receiveAsFlow()


    fun sendUiIntent(uiIntent: I) {
        viewModelScope.launch {
            _uiIntentFlow.send(uiIntent)
        }
    }

    init {
        viewModelScope.launch {
            uiIntentFlow.collect {
                handleIntent(it)
            }
        }
    }


    open fun initUiState(): S {
        val genType = javaClass.genericSuperclass
        return if (genType is ParameterizedType) {
            val type = genType.actualTypeArguments[0]
            if (type is Class<*>) {
                type.newInstance() as S
            } else {
                throw IllegalArgumentException("Illegal type")
            }
        } else {
            throw IllegalArgumentException("Illegal type")
        }
    }

    protected fun setState(reducer: S.() -> S) {
        stateStore.set {
            val state = this.reducer()
            state
        }
    }

    protected fun withState(action: (state: S) -> Unit) {
        stateStore.get(action)
    }


    protected abstract suspend fun handleIntent(intent: I)

    protected open fun <T : Any?> (suspend () -> T).execute(
        dispatcher: CoroutineDispatcher? = null,
        retainValue: KProperty1<S, Async<T>>? = null,
        reducer: S.(Async<T>) -> S
    ): Job {
        setState { reducer(Loading(value = retainValue?.get(this)?.invoke())) }
        return viewModelScope.launch(dispatcher ?: EmptyCoroutineContext) {
            try {
                val result = invoke()
                setState { reducer(Success(result)) }
            } catch (e: CancellationException) {
                @Suppress("RethrowCaughtException")
                throw e
            } catch (@Suppress("TooGenericExceptionCaught") e: Throwable) {
                setState { reducer(Fail(e, value = retainValue?.get(this)?.invoke())) }
            }
        }
    }

    protected open fun <T : Any?> Deferred<T>.execute(
        dispatcher: CoroutineDispatcher? = null,
        retainValue: KProperty1<S, Async<T>>? = null,
        reducer: S.(Async<T>) -> S
    ) = suspend { await() }.execute(dispatcher, retainValue, reducer)


    fun cancel(){
        onCleared()
    }


    override fun onCleared() {
        super.onCleared()
        viewModelScope.cancel()
    }
}