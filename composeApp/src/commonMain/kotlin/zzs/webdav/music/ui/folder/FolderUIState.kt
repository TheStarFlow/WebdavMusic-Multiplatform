package zzs.webdav.music.ui.folder

import zzs.webdav.music.base.Async
import zzs.webdav.music.base.IIntent
import zzs.webdav.music.base.IState
import zzs.webdav.music.base.Uninitialized
import zzs.webdav.music.bean.FileDesc

data class FolderUIState(
    //当前目录的资源
    val currDavList: Async<List<FileDesc>> = Uninitialized,
    //当前目录的媒体资源
    val currMediaDavList: List<FileDesc> = emptyList(),
    val focusIndex: Int = 0,
    val currTargetPath: String = ""
) : IState


sealed class FolderIntent : IIntent {

}