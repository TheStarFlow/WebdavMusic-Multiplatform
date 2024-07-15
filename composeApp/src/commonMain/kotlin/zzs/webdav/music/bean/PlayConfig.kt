package zzs.webdav.music.bean

enum class PlayMode(val modeName:String) {
    LIST("列表顺序"),
    LIST_LOOP("列表循环"),
    SINGLE_LOOP("单曲循环"),
    RANDOM("随机播放")
}