package zzs.webdav.music.bean


enum class FileFormat {
    FLAC,
    MP3,
    DIRECTORY,
    UNKNOW
}

enum class LrcFormat {
    LRC
}


val MediaFormat = listOf(FileFormat.FLAC, FileFormat.MP3)

open class FileDesc(val name: String, val format: FileFormat, val path: String) {
    override fun toString(): String {
        return "FileDesc(name='$name', format=$format, path='$path')"
    }
}


class Song(name: String, format: FileFormat, path: String, val lrcPath: String?) :
    FileDesc(name, format, path) {

    var singName: String? = null
    var singer: String? = null

    override fun toString(): String {
        return super.toString() + "Song(lrcPath=$lrcPath)"
    }
}

fun Song.fetchSingName(): String? {
    if (singName != null) return singName
    singName = name.substringAfter("-").substringBeforeLast(".")
    return singName
}

fun Song.fetchSingerName():String?{
    if (singer!=null)return singer
    singer = name.substringBefore("-")
    return singer
}


fun Song.httpPath(serverDesc: ServerDesc) = "http://${serverDesc.ip}:${serverDesc.port}${path}"

fun Song.lrcHttpPath(serverDesc: ServerDesc) = "http://${serverDesc.ip}:${serverDesc.port}${lrcPath}"

data class LyricEntity(
    val time: Long,
    val lyricText: String,
)
