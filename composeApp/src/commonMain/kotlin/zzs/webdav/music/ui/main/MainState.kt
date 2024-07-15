package zzs.webdav.music.ui.main

import zzs.webdav.music.base.Async
import zzs.webdav.music.base.IIntent
import zzs.webdav.music.base.IState
import zzs.webdav.music.base.Uninitialized
import zzs.webdav.music.bean.ServerDesc

data class MainState(val currServer:Async<ServerDesc?> = Uninitialized) : IState

sealed class MainIntent : IIntent {

    data object Add : MainIntent()

    data object FetchServer : MainIntent()

}