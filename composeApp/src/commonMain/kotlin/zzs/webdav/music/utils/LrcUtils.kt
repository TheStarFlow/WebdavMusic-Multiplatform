package zzs.webdav.music.utils

import zzs.webdav.music.bean.LyricEntity
import java.io.File
import java.io.InputStream
import java.io.InputStreamReader
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets
import java.nio.file.FileSystem

class LrcUtils {

    companion object {


        fun parseLyrics(lyrics: List<String>): List<LyricEntity> {
            val parsedLyrics = mutableListOf<LyricEntity>()
            val regex = Regex("\\[(\\d{2}:\\d{2}\\.\\d{1,3})](.*)")

            for (line in lyrics) {
                val matchResult = regex.find(line)
                if (matchResult != null) {
                    var sourceText: String = ""
                    var time:Long = 0L
                    var lyric:String = ""
                    for (i in 0 until matchResult.groups.size) {
                        val text = matchResult.groups[i]?.value ?: ""
                        when (i) {
                            0 -> {
                                sourceText = text
                            }

                            1 -> {
                                val (min,sec,mill) = parseTime(text)
                                time  = (min* 60L) * 1000 + sec* 1000 + mill

                            }

                            2 -> {
                                lyric = text
                            }
                        }
                    }
                    parsedLyrics.add(LyricEntity(time,lyric))

                }
            }

            return parsedLyrics
        }
        fun parseTime(timeString: String): Triple<Int, Int, Int> {
            val parts = timeString.split(":")
            val minutes = parts[0].toInt()
            val secondsAndMilliseconds = parts[1].split(".")
            val seconds = secondsAndMilliseconds[0].toInt()
            val milliseconds = secondsAndMilliseconds[1].toInt()
            return Triple(minutes, seconds, milliseconds)
        }

        private fun parseTimeToMillis(timeString: String): Long {
            val (minutes, seconds, millis) = timeString.split(":")
                .flatMap { it.split(".") }
                .map { it.toLong() }
            return (minutes * 60 * 1000) + (seconds * 1000) + millis
        }

        fun readLyricContent(ins: InputStream): String? {
            val badCode = "�"
            return try {
                val insReader = InputStreamReader(ins, "GB2312")
                val content = insReader.readText()
                content.trim()
            } catch (e: Exception) {
                return null
            }finally {
                ins.close()
            }
        }
    }
}