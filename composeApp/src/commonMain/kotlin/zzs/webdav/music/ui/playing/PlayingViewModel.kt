package zzs.webdav.music.ui.playing

import kotlinx.coroutines.Dispatchers
import org.koin.compose.getKoin
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf
import zzs.webdav.music.base.BaseViewModel
import zzs.webdav.music.base.Fail
import zzs.webdav.music.bean.FileDesc
import zzs.webdav.music.bean.FileFormat
import zzs.webdav.music.bean.LyricEntity
import zzs.webdav.music.bean.PlayMode
import zzs.webdav.music.bean.ServerDesc
import zzs.webdav.music.bean.Song
import zzs.webdav.music.data.repository.WebdavRepository
import zzs.webdav.music.player.IPlayer
import zzs.webdav.music.utils.LrcUtils

class PlayingViewModel(
    private val repository: WebdavRepository,
    private val currServer: ServerDesc
) :
    BaseViewModel<PlayingUIState, PlayingIntent>(), KoinComponent {

    private val player: IPlayer by inject {
        parametersOf(currServer)
    }

    override suspend fun handleIntent(intent: PlayingIntent) {

    }

    fun getLrcContent(serverDesc: ServerDesc, song: Song) {
        if (song.lrcPath?.isBlank() == true) {
            setState {
                copy(
                    lrcList = Pair(song, emptyList()),
                    lrcListState = Fail<List<LyricEntity>>(IllegalStateException())
                )
            }
            return
        }
        suspend {
            val lrc = repository.getLrcContent(serverDesc, song.lrcPath!!)
            if (lrc.isEmpty()) {
                emptyList()
            } else {
                val lyricList = LrcUtils.parseLyrics(lrc.lines())
                lyricList
            }
        }.execute(Dispatchers.IO) {
            copy(
                lrcList = Pair(song, it() ?: emptyList()),
                lrcListState = it
            )
        }

    }

    fun addMediaList(currServer: ServerDesc, mediaList: List<FileDesc>) {
        withState { old ->
            val songs: List<Song> = mediaList.takeWhile { it is Song }.map { it as Song }
            val playList = old.playList.toMutableList()
            playList.addAll(songs)
            player.addMediaItemList(currServer, songs)
            setState {
                //setState 闭包会执行两次
                copy(
                    playList = playList
                )
            }
        }
    }

    fun setMediaList(currServer: ServerDesc, mediaList: List<FileDesc>) {
        val songs: List<Song> = mediaList.takeWhile { it is Song }.map { it as Song }
        setState {
            copy(
                playList = songs
            )
        }
        player.setMediaItemList(serverDesc = currServer, songs)
    }


    fun clickMedia(currServer: ServerDesc, song: FileDesc) {
        if (song !is Song) return
        val currIndex = player.getMediaItemPosition()
        withState { old ->
            when (song.format) {
                FileFormat.FLAC, FileFormat.MP3 -> {
                    val currPlayList = old.playList.toMutableList()
                    when (PlayMode.entries[old.playMode]) {
                        PlayMode.LIST, PlayMode.LIST_LOOP -> {
                            if (currPlayList.isEmpty()) {
                                //原来没在播放直接播放
                                player.setMediaItem(currServer, song)
                                currPlayList.add(song)
                            } else {
                                val newIndex = currIndex + 1
                                currPlayList.add(newIndex, song)
                                player.apply {
                                    addMediaItem(currServer, newIndex, song)
                                    seekToNextMediaItem(newIndex, 0)
                                }
                            }
                        }

                        PlayMode.SINGLE_LOOP -> {
                            player.setMediaItem(currServer, song)
                            currPlayList.clear()
                            currPlayList.add(song)
                        }

                        PlayMode.RANDOM -> {
                            currPlayList.add(0, song)
                            player.apply {
                                setMediaList(currServer, currPlayList)
                                seekToNextMediaItem(0, 0)
                            }
                        }
                    }
                    setState {
                        copy(
                            currPlayingSong = song,
                            playList = currPlayList
                        )
                    }
                }

                else -> {}
            }
        }

    }


    fun setPlayMode() {
        withState { old ->
            var newMode = old.playMode + 1
            if (newMode >= PlayMode.entries.size) {
                newMode = 0
            }
            suspend {
                player.setPlayMode(PlayMode.entries[newMode])
                repository.setPlayMode(newMode)
            }.execute(Dispatchers.Main) {
                //exe闭包也会执行两次
                copy(playMode = newMode)
            }
        }
    }

    fun playPreMediaItem() :Boolean {
        return player.playPreMedia()
    }

    fun playNextMediaItem(): Boolean {
        return player.playNextMedia()
    }

    fun playOrPause() {
        player.run {
            if (isPlaying()) {
                pause()
            } else {
                play()
            }
        }
    }

    fun playItem(index: Int) {
        val currIndex = player.getMediaItemPosition()
        val isPlaying = player.isPlaying()
        if (currIndex == index) {
            if (isPlaying) {
                player.pause()
            } else {
                player.play()
            }
        } else {
            player.seekToNextMediaItem(index, 0)
        }
    }

    fun seekMediaTo(progress: Float) {
        player.seekTo(progress.toLong())
    }

}