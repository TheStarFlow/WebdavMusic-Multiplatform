package zzs.webdav.music.utils

import org.koin.core.logger.Level
import org.koin.core.logger.PrintLogger


val logger = PrintLogger()


fun logInfo(content: String) {
    logger.log(Level.INFO, content)
}