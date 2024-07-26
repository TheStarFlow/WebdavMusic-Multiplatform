package zzs.webdav.music.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstrainedLayoutReference
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintLayoutScope
import androidx.constraintlayout.compose.Dimension
import me.webdav.resources.Res
import me.webdav.resources.add_playlist
import me.webdav.resources.playlist
import me.webdav.resources.quit
import me.webdav.resources.setting
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject
import org.koin.core.parameter.ParametersHolder
import org.koin.core.parameter.parametersOf
import zzs.webdav.music.bean.FileDesc
import zzs.webdav.music.bean.MenuItem
import zzs.webdav.music.bean.ServerDesc
import zzs.webdav.music.extension.collectAsState
import zzs.webdav.music.extension.focusClick
import zzs.webdav.music.locals.LocalAppQuit
import zzs.webdav.music.locals.LocalPlayListSwitcher
import zzs.webdav.music.player.IPlayer
import zzs.webdav.music.theme.TextColor
import zzs.webdav.music.ui.dialog.AppQuitDialog
import zzs.webdav.music.ui.dialog.ModifyServerDialog
import zzs.webdav.music.ui.dialog.SettingDialog
import zzs.webdav.music.ui.folder.DavDetailScreen
import zzs.webdav.music.ui.main.MainViewModel
import zzs.webdav.music.ui.playing.PlayingUIState
import zzs.webdav.music.ui.playing.PlayingViewModel


@Composable
fun HomeScreen(
    currServer: ServerDesc,
) {
    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 40.dp, end = 40.dp, top = 24.dp, bottom = 24.dp)
    ) {
        val (left, right) = createRefs()
        var currFolderMediaList by remember {
            mutableStateOf<List<FileDesc>>(emptyList())
        }
        var showSetting by remember {
            mutableStateOf(false)
        }
        var showQuit by remember {
            mutableStateOf(false)
        }
        LeftLayout(currServer, left = left, currFolderMediaList,
            onQuit = {
                showQuit = true
            },
            onCallSetting = {
                showSetting = true
            })
        RightLayout(
            currServer,
            left = left,
            right = right
        ) {
            currFolderMediaList = it
        }
        val mainViewModel: MainViewModel = koinInject()
        var showAddServer by remember {
            mutableStateOf(false)
        }
        var showEditServer by remember {
            mutableStateOf(false)
        }
        var editServer by remember {
            mutableStateOf<ServerDesc?>(null)
        }
        if (showSetting) {
            SettingDialog(
                editServer = { server ->
                    editServer = server
                    showEditServer = true
                    showSetting = false
                },
                addServer = {
                    showAddServer = true
                    showSetting = false
                },
                dismiss = {
                    showSetting = false
                }
            )
        }
        val quit = LocalAppQuit.current
        if (showQuit) {
            AppQuitDialog(
                quit = quit, dismiss = {
                    showQuit = false
                }
            )
        }
        if (showAddServer || showEditServer) {
            ModifyServerDialog(
                modifyServer = mainViewModel::modifyServer,
                currServer = editServer
            ) {
                showAddServer = false
                showEditServer = false
            }
        }
    }
}

@Composable
internal fun ConstraintLayoutScope.RightLayout(
    currServer: ServerDesc,
    left: ConstrainedLayoutReference,
    right: ConstrainedLayoutReference,
    currMediaListCallBack: ((List<FileDesc>) -> Unit)? = null
) {
    Box(modifier = Modifier
        .constrainAs(right) {
            start.linkTo(left.end, 24.dp)
            end.linkTo(parent.end)
            top.linkTo(parent.top)
            bottom.linkTo(parent.bottom)
            width = Dimension.fillToConstraints
            height = Dimension.fillToConstraints
        }) {
        DavDetailScreen(currServer = currServer, currMediaListCallBack)
    }
}


@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ConstraintLayoutScope.LeftLayout(
    currServer: ServerDesc,
    left: ConstrainedLayoutReference,
    currPageMediaList: List<FileDesc>,
    onQuit: () -> Unit,
    onCallSetting: () -> Unit,
    playingViewModel: PlayingViewModel = koinInject<PlayingViewModel>() { parametersOf(currServer) }
) {
    val playList by playingViewModel.collectAsState(prop1 = PlayingUIState::playList)
    val playListEnable = remember(playList.size, currServer) {
        playList.isNotEmpty()
    }
    val mediaListOpEnable = currPageMediaList.isNotEmpty()
    val playListSwitcher = LocalPlayListSwitcher.current
    val menuItems = remember(currPageMediaList, currPageMediaList.size, currServer) {
        listOf(
            MenuItem(
                drawableId = Res.drawable.setting,
                name = "设置",
                enable = true,
                call = onCallSetting
            ),
            MenuItem(
                drawableId = Res.drawable.quit,
                name = "退出",
                enable = true,
                call = onQuit
            ),
            MenuItem(
                drawableId = Res.drawable.playlist,
                name = "播放列表",
                call = {
                    playListSwitcher.value = true
                }
            ),
            MenuItem(
                drawableId = Res.drawable.add_playlist,
                name = "添加到播放列表",
                call = {
                    playingViewModel.addMediaList(currServer, currPageMediaList)
                    //ToastUtils.showToast("已添加到播放列表")
                }
            ),
            MenuItem(
                drawableId = Res.drawable.add_playlist,
                name = "替换播放列表",
                call = {
                    playingViewModel.setMediaList(currServer, currPageMediaList)
                    //ToastUtils.showToast("已替换播放列表")

                }
            ))
    }
    Surface(
        modifier = Modifier
            .constrainAs(left) {
                start.linkTo(parent.start)
                top.linkTo(parent.top)
                bottom.linkTo(parent.bottom)
            }
            .width(270.dp), color = Color.Gray.copy(alpha = 0.4f),
        shape = RoundedCornerShape(8.dp)
    ) {
        ConstraintLayout(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 20.dp)
        ) {
            val itemsLayout = createRef()
            var focusIndex by remember { mutableIntStateOf(0) }
            LazyColumn(modifier = Modifier.constrainAs(itemsLayout) {
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                top.linkTo(parent.top)
                bottom.linkTo(parent.bottom)
                height = Dimension.fillToConstraints
                width = Dimension.fillToConstraints
            }, horizontalAlignment = Alignment.Start) {
                itemsIndexed(menuItems, key = { _, item ->
                    item.name + item.drawableId
                }) { index, item ->
                    val enable = {
                        when (index) {
                            0, 1 -> {
                                true
                            }

                            2 -> {
                                playListEnable
                            }

                            3, 4 -> {
                                mediaListOpEnable
                            }

                            else -> {
                                true
                            }
                        }
                    }
                    val focusRequester = remember { FocusRequester() }
                    var isFocused by remember { mutableStateOf(false) }
                    Menu(modifier = Modifier
                        .background(color = if (isFocused) MaterialTheme.colors.onSecondary else Color.Transparent)
                        .padding(start = 12.dp, top = 8.dp, bottom = 8.dp, end = 12.dp)
                        .fillMaxWidth()
                        .focusProperties {
                            up = if (index == 0) {
                                FocusRequester.Cancel
                            } else {
                                FocusRequester.Default
                            }
                        }
                        .focusClick(focusRequester, enable = enable, onFocusChanged = {
                            isFocused = it.isFocused
                            if (isFocused) {
                                focusIndex = index
                            }
                        }, onClick = item.call),
                        text = item.name,
                        drawable = item.drawableId,
                        enable = enable
                    )
                    if (focusIndex == index) {
                        LaunchedEffect(key1 = focusIndex) {
                            if (focusIndex == index) {
                                focusRequester.requestFocus()
                            }
                        }
                    }
                }
            }
        }

    }
}


//学习焦点设置 以最原始的方式进行，不封装先

@Composable
fun Menu(
    modifier: Modifier,
    text: String,
    drawable: DrawableResource,
    enable: () -> Boolean = { true },
) {
    Row(
        modifier = modifier
    ) {
        Image(
            modifier = Modifier.size(30.dp),
            painter = painterResource(drawable),
            contentDescription = null,
            colorFilter = ColorFilter.tint(
                if (enable()) MaterialTheme.colors.primary else MaterialTheme.colors.primary.copy(
                    alpha = 0.5f
                )
            )
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = text,
            color = if (enable()) TextColor else TextColor.copy(alpha = 0.5f),
            fontSize = 24.sp
        )
    }
}