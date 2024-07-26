//package zzs.webdav.music.player
//
//import uk.co.caprica.vlcj.player.base.MediaPlayer
//import uk.co.caprica.vlcj.player.base.MediaPlayerEventAdapter
//import uk.co.caprica.vlcj.player.component.AudioPlayerComponent
//import zzs.webdav.music.bean.PlayMode
//import zzs.webdav.music.bean.ServerDesc
//import zzs.webdav.music.bean.Song
//
//
//class DesktopPlayer : IPlayer {
//
//
//    private val mCurrPlayList = mutableListOf<Song>()
//    private val audioComponent = AudioPlayerComponent()
//    private val audioPlayer = audioComponent.mediaPlayer()
//    private var isPause = false
//    private var isPlaying = false
//    private var mode = PlayMode.LIST
//    private var currMediaIndex = -1
//
//    init {
//        audioPlayer.events().addMediaPlayerEventListener(object : MediaPlayerEventAdapter() {
//            override fun playing(mediaPlayer: MediaPlayer?) {
//                isPlaying = true
//            }
//
//            override fun stopped(mediaPlayer: MediaPlayer?) {
//                isPlaying = false
//                isPause = false
//            }
//
//            override fun paused(mediaPlayer: MediaPlayer?) {
//                isPause = true
//            }
//        })
//    }
//
//    override fun getMediaItem(): MyMediaItem? {
//        return null
//    }
//
//    override fun seekTo(positionMs: Long) {
//        audioPlayer.controls().setPosition(positionMs.toFloat())
//    }
//
//    override fun playNextMedia(): Boolean {
//        if (isPlaying()) {
//            audioPlayer.controls().stop()
//        } else {
//
//        }
//    }
//
//    override fun playPreMedia(): Boolean {
//
//    }
//
//    override fun getDuration(): Long {
//        return audioPlayer.media().info().duration()
//    }
//
//    override fun play() {
//        audioPlayer.controls().play()
//    }
//
//    override fun pause() {
//        audioPlayer.controls().pause()
//    }
//
//    override fun isPlaying(): Boolean {
//        return isPlaying
//    }
//
//    override fun seekToNextMediaItem(index: Int, positionMs: Long) {
//
//    }
//
//    override fun getMediaItemPosition(): Int {
//
//    }
//
//    override fun addMediaItem(serverDesc: ServerDesc, index: Int, song: Song) {
//        mCurrPlayList.add(index, song)
//    }
//
//    override fun addMediaItem(serverDesc: ServerDesc, song: Song) {
//        mCurrPlayList.add(song)
//    }
//
//    override fun setMediaItem(serverDesc: ServerDesc, song: Song) {
//        mCurrPlayList.clear()
//        mCurrPlayList.add(song)
//        currMediaIndex = 0
//        audioPlayer.media().play(song.path)
//    }
//
//    override fun addMediaItemList(serverDesc: ServerDesc, list: List<Song>) {
//        mCurrPlayList.addAll(list)
//    }
//
//    override fun setMediaItemList(serverDesc: ServerDesc, list: List<Song>) {
//        mCurrPlayList.clear()
//        mCurrPlayList.addAll(list)
//    }
//
//    override fun setPlayMode(playMode: PlayMode) {
//        mode = playMode
//    }
//
//
//    private fun getNextMedia(): Song {
//        when (mode) {
//            PlayMode.LIST -> {
//
//            }
//
//            PlayMode.LIST_LOOP -> {
//
//            }
//
//            PlayMode.SINGLE_LOOP -> {
//
//            }
//
//            PlayMode.RANDOM -> {
//
//            }
//        }
//    }
//
//}