package zzs.webdav.music.ui.playing

import zzs.webdav.music.base.Async
import zzs.webdav.music.base.IState
import zzs.webdav.music.base.Uninitialized
import zzs.webdav.music.bean.LyricEntity
import zzs.webdav.music.bean.Song


data class PlayingUIState(
    val playList: List<Song> = emptyList(),
    val currPlayingSong: Song? = null,
    val isPlaying: Boolean = false,
    val playListPosition: Int = -1,
    val duration:Long = 0L,
    val playMode:Int = 0,
    val playerState:Int = 0,
    val lrcList:Pair<Song?,List<LyricEntity>> = Pair(null, emptyList()),//0->列表顺序 1->列表循环 2->单曲循环 3->随机播放（无限随机）
    val lrcListState: Async<List<LyricEntity>> = Uninitialized,//0->列表顺序 1->列表循环 2->单曲循环 3->随机播放（无限随机）
    val currPlayPosition:Long = 0L,
) : IState