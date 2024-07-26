package zzs.webdav.music.player

import android.app.Application
import android.os.Handler
import androidx.annotation.OptIn
import androidx.media3.common.AudioAttributes
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.Player.REPEAT_MODE_ALL
import androidx.media3.common.Player.REPEAT_MODE_OFF
import androidx.media3.common.Player.REPEAT_MODE_ONE
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DataSource
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.DefaultRenderersFactory
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.Renderer
import androidx.media3.exoplayer.RenderersFactory
import androidx.media3.exoplayer.audio.AudioRendererEventListener
import androidx.media3.exoplayer.audio.MediaCodecAudioRenderer
import androidx.media3.exoplayer.mediacodec.MediaCodecSelector
import androidx.media3.exoplayer.metadata.MetadataOutput
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.exoplayer.text.TextOutput
import androidx.media3.exoplayer.video.VideoRendererEventListener
import zzs.webdav.music.bean.PlayMode
import zzs.webdav.music.bean.ServerDesc
import zzs.webdav.music.bean.Song
import zzs.webdav.music.bean.httpPath
import zzs.webdav.music.utils.credentials


class AndroidPlayer @OptIn(UnstableApi::class) constructor(
    private val currServerDesc: ServerDesc,
    private val context: Application,
) : IPlayer {
    private val player: Player

    init {
        val httpDataSourceFactory = DefaultHttpDataSource.Factory()
        val dataSourceFactory = DataSource.Factory {
            val datasource = httpDataSourceFactory.createDataSource()
            datasource.setRequestProperty(
                "Authorization",
                credentials(currServerDesc.user, currServerDesc.password)
            )
            datasource
        }
        //仅仅需要播放音频
        val audioOnlyRenderersFactory =
            RenderersFactory {
                    handler: Handler,
                    _: VideoRendererEventListener,
                    audioListener: AudioRendererEventListener,
                    _: TextOutput,
                    _: MetadataOutput,
                ->
                arrayOf<Renderer>(
                    MediaCodecAudioRenderer(
                        context.applicationContext,
                        MediaCodecSelector.DEFAULT,
                        handler,
                        audioListener
                    )
                )
            }
        player = ExoPlayer.Builder(context, audioOnlyRenderersFactory)
            .setAudioAttributes(AudioAttributes.DEFAULT, true)
            .setRenderersFactory(
                DefaultRenderersFactory(context)
                    .setExtensionRendererMode(DefaultRenderersFactory.EXTENSION_RENDERER_MODE_ON)
            )
            .setMediaSourceFactory(
                DefaultMediaSourceFactory(context).setDataSourceFactory(
                    dataSourceFactory
                )
            )
            .setHandleAudioBecomingNoisy(true)
            .build()
        player.repeatMode = REPEAT_MODE_OFF
        player.addListener(object : Player.Listener {
            override fun onPlayerError(error: PlaybackException) {
                super.onPlayerError(error)
            }
        })
    }

    override fun getMediaItem(): MyMediaItem {
        return MyMediaItem().apply {
            name = player.currentMediaItem?.mediaId
            path =
                (player.currentMediaItem?.localConfiguration?.tag as Song?)?.httpPath(currServerDesc)
        }
    }

    override fun seekTo(positionMs: Long) {
        player.seekTo(positionMs)
    }

    override fun playNextMedia(): Boolean {
        if (player.hasNextMediaItem()) {
            player.seekToNextMediaItem()
            return true
        }
        return false
    }

    override fun playPreMedia(): Boolean {
        if (player.hasPreviousMediaItem()) {
            player.seekToPreviousMediaItem()
            return true
        }
        return false
    }

    override fun getDuration(): Long {
        return player.duration
    }

    override fun play() {
        player.play()
    }

    override fun pause() {
        player.pause()
    }

    override fun isPlaying() = player.isPlaying

    override fun seekToNextMediaItem(index: Int, positionMs: Long) {
        player.seekTo(index, positionMs)
    }

    override fun getMediaItemPosition(): Int {
        return player.currentMediaItemIndex
    }

    override fun addMediaItem(serverDesc: ServerDesc, index: Int, song: Song) {
        player.addMediaItem(
            index,
            MediaItem.Builder().setMediaId(song.name).setUri(song.httpPath(serverDesc)).setTag(song)
                .build()
        )
    }

    override fun addMediaItem(serverDesc: ServerDesc, song: Song) {
        player.addMediaItem(
            MediaItem.Builder().setMediaId(song.name).setUri(song.httpPath(serverDesc)).setTag(song)
                .build()
        )
    }

    override fun setMediaItem(serverDesc: ServerDesc, song: Song) {
        player.setMediaItem(
            MediaItem.Builder().setMediaId(song.name).setUri(song.httpPath(serverDesc)).setTag(song)
                .build()
        )
        prepareAndPlay()
    }

    override fun addMediaItemList(serverDesc: ServerDesc, list: List<Song>) {
        val items = list.map { song ->
            MediaItem.Builder().setMediaId(song.name).setUri(song.httpPath(serverDesc)).setTag(song)
                .build()
        }
        player.addMediaItems(items)
        if (player.mediaItemCount == 0 || !player.isPlaying) {
            prepareAndPlay()
        }
    }

    override fun setMediaItemList(serverDesc: ServerDesc, list: List<Song>) {
        val items = list.map { song ->
            MediaItem.Builder().setMediaId(song.name).setUri(song.httpPath(serverDesc)).setTag(song)
                .build()
        }
        player.setMediaItems(items)
        prepareAndPlay()
    }

    override fun setPlayMode(playMode: PlayMode) {
        when (playMode) {
            PlayMode.LIST -> {
                player.repeatMode = REPEAT_MODE_OFF
                player.shuffleModeEnabled = false
            }

            PlayMode.LIST_LOOP -> {
                player.repeatMode = REPEAT_MODE_ALL
                player.shuffleModeEnabled = false
            }

            PlayMode.SINGLE_LOOP -> {
                player.repeatMode = REPEAT_MODE_ONE
                player.shuffleModeEnabled = false
            }

            PlayMode.RANDOM -> {
                player.repeatMode = REPEAT_MODE_ALL
                player.shuffleModeEnabled = true
            }
        }
    }

    private fun prepareAndPlay() {
        player.prepare()
        player.play()
    }

}