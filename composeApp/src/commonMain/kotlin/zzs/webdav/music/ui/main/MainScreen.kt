package zzs.webdav.music.ui.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import org.koin.compose.koinInject
import zzs.webdav.music.base.Async
import zzs.webdav.music.base.BasicCompose
import zzs.webdav.music.base.Loading
import zzs.webdav.music.base.Success
import zzs.webdav.music.base.Uninitialized
import zzs.webdav.music.bean.ServerDesc
import zzs.webdav.music.extension.collectAsState
import zzs.webdav.music.locals.LocalAppNavigator
import zzs.webdav.music.locals.LocalAppQuit
import zzs.webdav.music.locals.LocalFolderBackButtonState
import zzs.webdav.music.locals.LocalPlayListState
import zzs.webdav.music.locals.LocalPlayListSwitcher
import zzs.webdav.music.locals.LocalScreenSize
import zzs.webdav.music.route.AppNavigatorImpl
import zzs.webdav.music.route.Screen
import zzs.webdav.music.theme.themeColors
import zzs.webdav.music.ui.MyLoading
import zzs.webdav.music.ui.empty.EmptyScreen
import zzs.webdav.music.ui.home.HomeScreen
import zzs.webdav.music.utils.logInfo


@Composable
fun MainScreen(
    dpWidth: Float = 1280f,
    dpHeight: Float = 720f,
    showBack:Boolean = false,
    userWidth: Boolean = true,
    appQuit: () -> Unit = {}
) {
    val appNavigator = remember {
        AppNavigatorImpl()
    }
    val showPlayList = remember {
        mutableStateOf(false)
    }
    var screenSize by remember { mutableStateOf(Size(0f, 0f)) }
    val targetDensityWidth = remember(screenSize.width) { screenSize.width / dpWidth }
    val targetDensityHeight = remember(screenSize.height) { screenSize.height / dpHeight }
    MaterialTheme(colors = themeColors) {
        Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colors.background)
            .drawWithCache {
                screenSize = size
                onDrawWithContent {
                    drawContent()
                }
            }) {
            val density = remember(screenSize.width, screenSize.height, screenSize) {
                if (userWidth) Density(density = targetDensityWidth) else Density(density = targetDensityHeight)

            }
            CompositionLocalProvider(
                LocalAppNavigator provides appNavigator,
                LocalAppQuit provides appQuit,
                LocalPlayListSwitcher provides showPlayList,
                LocalScreenSize provides screenSize,
                LocalFolderBackButtonState provides showBack,
                LocalDensity provides if (targetDensityWidth == 0f) LocalDensity.current else density
            ) {
                WebdavScreen()
                if (showPlayList.value) {

                }
            }
        }
    }

}


@Composable
fun WebdavScreen(viewModel: MainViewModel = koinInject<MainViewModel>()) {
    val appNavigator = LocalAppNavigator.current
    val currServer by viewModel.collectAsState(MainState::currServer)
    var currStart by remember { mutableStateOf(Screen.LoadingScreen.route) }
    LaunchedEffect(currServer()) {
        currStart = when (currServer) {
            is Uninitialized, is Loading -> {
                Screen.LoadingScreen.route
            }

            is Success -> {
                if (currServer() != null) {
                    Screen.HomeScreen.route
                } else {
                    Screen.EmptyDavServerScreen.route
                }
            }

            else -> {
                Screen.EmptyDavServerScreen.route
            }
        }
        appNavigator.push(currStart)
    }
    NavHost(appNavigator.navController, startDestination = currStart) {
        composable(route = Screen.LoadingScreen.route) {
            MyLoading(modifier = Modifier.fillMaxSize())
        }
        composable(route = Screen.EmptyDavServerScreen.route) {
            EmptyScreen(modifyServer = { serverDesc, insert ->
                viewModel.modifyServer(serverDesc, insert, updateCurr = true)
            })
        }
        composable(route = Screen.HomeScreen.route) {
            HomeScreen(currServer()!!)
        }
        composable(route = Screen.PlayDetailScreen.route) {
            //  PlayDetailScreen()
        }
    }
}


@Composable
fun smallCase(
    viewModel: MainViewModel,
    state: Async<ServerDesc?>
) {
    Button(onClick = {
        viewModel.sendUiIntent(MainIntent.Add)
    }) {
        Text("$state")
    }
}

