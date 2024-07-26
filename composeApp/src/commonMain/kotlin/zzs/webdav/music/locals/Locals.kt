package zzs.webdav.music.locals

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.geometry.Size
import zzs.webdav.music.route.AppNavigator

val LocalAppNavigator = staticCompositionLocalOf<AppNavigator> { error("Navigator not provide") }

val LocalAppQuit = staticCompositionLocalOf<() -> Unit> { error("App quit method not provide") }

val LocalPlayListState = compositionLocalOf { false }

val LocalScreenSize = compositionLocalOf { Size(0f, 0f) }

val LocalFolderBackButtonState = compositionLocalOf { false }


val LocalPlayListSwitcher =
    staticCompositionLocalOf<MutableState<Boolean>> { error("Play list Switcher not provide") }

val LocalSettingDialogSwitcher =
    staticCompositionLocalOf<MutableState<Boolean>> { error("SettingDialogSwitcher not provide") }

val LocalQuitDialogSwitcher =
    staticCompositionLocalOf<MutableState<Boolean>> { error("Quit App Switcher not provide") }
