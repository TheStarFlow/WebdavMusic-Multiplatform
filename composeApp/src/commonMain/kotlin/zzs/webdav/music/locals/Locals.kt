package zzs.webdav.music.locals

import androidx.compose.runtime.staticCompositionLocalOf
import zzs.webdav.music.route.AppNavigator

val LocalAppNavigator = staticCompositionLocalOf<AppNavigator> { error("Navigator not provide") }
