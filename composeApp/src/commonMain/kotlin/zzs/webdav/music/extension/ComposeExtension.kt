package zzs.webdav.music.extension

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import zzs.webdav.music.base.BaseViewModel
import zzs.webdav.music.base.IIntent
import zzs.webdav.music.base.IState
import kotlin.reflect.KProperty
import kotlin.reflect.KProperty1


@Composable
fun <VM:BaseViewModel<S,I>,S: IState,I:IIntent,A> VM.collectAsState(prop1: KProperty1<S, A>):State<A>{
    val mappedFlow = remember(prop1) { uiStateFlow.map { prop1.get(it) }.distinctUntilChanged() }
    return mappedFlow.collectAsState(initial = initUiState().let { prop1.get(it) })
}