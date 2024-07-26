package zzs.webdav.music.player

import zzs.webdav.music.bean.PlayMode
import zzs.webdav.music.bean.ServerDesc
import zzs.webdav.music.bean.Song




interface IPlayer {

    fun getMediaItem(): MyMediaItem?

    fun seekTo(positionMs:Long)

    fun playNextMedia():Boolean

    fun playPreMedia():Boolean

    fun getDuration():Long

    fun play()

    fun pause()

    fun isPlaying():Boolean

    fun seekToNextMediaItem(index:Int,positionMs: Long)

    fun getMediaItemPosition():Int

    fun addMediaItem(serverDesc: ServerDesc, index: Int, song: Song)

    fun addMediaItem(serverDesc: ServerDesc, song: Song)

    fun setMediaItem(serverDesc: ServerDesc, song: Song)

    fun addMediaItemList(serverDesc: ServerDesc, list: List<Song>)

    fun setMediaItemList(serverDesc: ServerDesc, list: List<Song>)

    fun setPlayMode(playMode: PlayMode)
}