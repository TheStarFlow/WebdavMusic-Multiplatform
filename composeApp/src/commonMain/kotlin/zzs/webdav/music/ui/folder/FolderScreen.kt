package zzs.webdav.music.ui.folder

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.type
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import me.webdav.resources.Res
import me.webdav.resources.back
import me.webdav.resources.changpian_small
import me.webdav.resources.flac
import me.webdav.resources.folder
import me.webdav.resources.launcher
import me.webdav.resources.mp3
import me.webdav.resources.unknow
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.koinInject
import zzs.webdav.music.base.Fail
import zzs.webdav.music.base.Loading
import zzs.webdav.music.base.Success
import zzs.webdav.music.bean.FileDesc
import zzs.webdav.music.bean.FileFormat
import zzs.webdav.music.bean.ServerDesc
import zzs.webdav.music.bean.Song
import zzs.webdav.music.bean.fetchSingName
import zzs.webdav.music.bean.fetchSingerName
import zzs.webdav.music.extension.collectAsState
import zzs.webdav.music.extension.focusClick
import zzs.webdav.music.locals.LocalAppNavigator
import zzs.webdav.music.locals.LocalFolderBackButtonState
import zzs.webdav.music.locals.LocalPlayListState
import zzs.webdav.music.route.Screen
import zzs.webdav.music.theme.TextColor
import zzs.webdav.music.ui.MyLoading
import zzs.webdav.music.ui.playing.PlayingUIState
import zzs.webdav.music.ui.playing.PlayingViewModel
import zzs.webdav.music.utils.logInfo


@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun DavDetailScreen(
    currServer: ServerDesc,
    currMediaListCallBack: ((List<FileDesc>) -> Unit)? = null,
    playingViewModel: PlayingViewModel = koinInject<PlayingViewModel>()
) {
    val currPlaySong by playingViewModel.collectAsState(PlayingUIState::currPlayingSong)
    val isPlaying by playingViewModel.collectAsState(prop1 = PlayingUIState::isPlaying)
    val navController = rememberNavController()
    var isFocusedLeaved by remember {
        mutableStateOf(false)
    }
    val cdLayoutFocusRequester = remember {
        FocusRequester()
    }
    var cacheServer by remember {
        mutableStateOf(currServer)
    }
    LaunchedEffect(key1 = currServer) {
        if (currServer != cacheServer) {
            logInfo("switch server ")
            while (navController.popBackStack()) {
            }
            cacheServer = currServer
            navController.navigate(Screen.DavDetailScreen.route)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        NavHost(
            navController = navController,
            startDestination = Screen.DavDetailScreen.route
        ) {
            // 桌面平台 navigation 目前传参有 bug ， 先通过 server 的单例传递参数
            composable(
                route = Screen.DavDetailScreen.route,
//                arguments = listOf(navArgument("target") {
//                    nullable = true
//                    type = NavType.StringType
//                    defaultValue = currServer.targetPath
//                })
            ) {
                //val targetPath = it.arguments?.getString("target")?.replace("-","/")
                DavList(
                    currServer,
                    navController,
                    currPlaySong,
                    targetPath = null,
                    cdLayoutFocusRequester,
                    currMediaListCallBack
                ) {
                    isFocusedLeaved = it
                }
            }
        }
        if (currPlaySong != null) {
            var isCDFocused by remember {
                mutableStateOf(false)
            }
            val appNavigator = LocalAppNavigator.current
            CDLayout(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .focusProperties {
                        exit = {
                            when (it) {
                                FocusDirection.Left -> {
                                    FocusRequester.Default
                                }

                                else -> {
                                    FocusRequester.Cancel
                                }
                            }
                        }
                    }
                    .focusClick(focusRequester = cdLayoutFocusRequester, onFocusChanged = {
                        isCDFocused = it.isFocused
                    }) {
                        appNavigator.push(Screen.PlayDetailScreen.route)
                    },
                title = {
                    "${currPlaySong?.fetchSingName()}-${currPlaySong?.fetchSingerName()}"
                },
                isFocused = {
                    isCDFocused
                }
            ) { isPlaying }
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun DavList(
    currServer: ServerDesc,
    navController: NavController,
    currPlaySong: Song?,
    targetPath: String? = null,
    cdLayoutFocusRequester: FocusRequester,
    currMediaListCallBack: ((List<FileDesc>) -> Unit)? = null,
    folderViewModel: FolderViewModel = koinInject<FolderViewModel>(),
    playingViewModel: PlayingViewModel = koinInject<PlayingViewModel>(),
    focusLeave: (Boolean) -> Unit
) {
    val folderFocusRequester = remember {
        FocusRequester()
    }
    DisposableEffect(Unit) {
        folderViewModel.fetchServerDavResList(currServer, targetPath)
        onDispose {
            folderViewModel.cancel()
        }
    }
    val davList by folderViewModel.collectAsState(prop1 = FolderUIState::currDavList)
    val mediaList by folderViewModel.collectAsState(prop1 = FolderUIState::currMediaDavList)
    val focusIndex by folderViewModel.collectAsState(prop1 = FolderUIState::focusIndex)
    val currPath by folderViewModel.collectAsState(prop1 = FolderUIState::currTargetPath)
    val focusManager = LocalFocusManager.current
    val showBack = LocalFolderBackButtonState.current
    val davState = rememberLazyListState()
    val playListShow = LocalPlayListState.current
    LaunchedEffect(key1 = mediaList, key2 = mediaList.size) {
        if (mediaList.isNotEmpty()) {
            currMediaListCallBack?.invoke(mediaList)
        }
    }
    when (davList) {
        is Success -> {
            if (davList() == null) {
                MyLoading(modifier = Modifier.fillMaxSize())
            } else {
                LazyColumn(
                    state = davState,
                    modifier = Modifier
                        .focusProperties {
                            exit = {
                                focusLeave(true)
                                FocusRequester.Default
                            }
                        }
                        .focusRequester(folderFocusRequester)
                        .onFocusChanged {
                            if (it.isFocused) {
                                //folderFocusRequester.freeFocus()
                                focusManager.moveFocus(FocusDirection.Next)
                            }
                        }
                        .focusable(),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    if ((currPath.isNotBlank() || currPath != currServer.targetPath) && showBack) {
                        item {
                            Row(modifier = Modifier.fillMaxWidth().padding(
                                end = 10.dp,
                                top = 4.dp,
                                bottom = 4.dp
                            ).clickable {
                                navController.navigateUp()
                            }, verticalAlignment = Alignment.CenterVertically) {
                                Image(
                                    modifier = Modifier.size(24.dp),
                                    painter = painterResource(Res.drawable.back),
                                    contentDescription = null
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(text = "返回", fontSize = 26.sp, color = Color.White)
                            }
                        }
                    }
                    if (davList() != null) {
                        itemsIndexed(davList()!!, key = { _, file ->
                            file.path
                        }) { index, item ->
                            val focusRequester = remember { FocusRequester() }
                            LaunchedEffect(Unit) {
                                if (index == focusIndex) {
                                    focusRequester.requestFocus()
                                }
                            }
                            var isFocused by remember { mutableStateOf(false) }
                            DavItem(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(color = if (isFocused) MaterialTheme.colors.onSecondary else Color.Transparent)
                                    .padding(
                                        end = 10.dp,
                                        top = 4.dp,
                                        bottom = 4.dp
                                    )
                                    .focusProperties {
                                        if (currPlaySong != null) {
                                            right = cdLayoutFocusRequester
                                        }
                                    }
                                    .focusClick(focusRequester = focusRequester,
                                        onKeyEvent = {
                                            if (it.key == Key.Back && it.type == KeyEventType.KeyDown) {
                                                //播放列表正在展示，不拦截返回事件
                                                if (playListShow) return@focusClick false
                                            }
                                            false
                                        }, onFocusChanged = { state ->
                                            isFocused = state.isFocused
                                            if (isFocused) {
                                                folderViewModel.updateFocusIndex(index)
                                                focusLeave(false)
                                            }

                                        }) {
                                        when (item.format) {
                                            FileFormat.DIRECTORY -> {
                                                //桌面版本 navigation 传参会崩溃 先通过server 来传递
                                                currServer.targetPath = item.path
                                                navController.navigate(
                                                    Screen.DavDetailScreen.route
                                                )
                                            }

                                            FileFormat.MP3, FileFormat.FLAC -> {
                                                playingViewModel.clickMedia(currServer, item)
                                            }

                                            FileFormat.UNKNOW -> {

                                            }
                                        }
                                    },
                                fileFormat = item.format,
                                name = item.name
                            )
                        }
                    }
                }
            }
        }

        is Loading -> {
            MyLoading(modifier = Modifier.fillMaxSize())
        }

        is Fail -> {
            tryAgain {
                folderViewModel.fetchServerDavResList(currServer, targetPath)
            }
        }

        else -> {

        }
    }
}

@Composable
fun tryAgain(tryAgain: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.align(Alignment.Center)
        ) {
            Text(text = "网络好像有点问题...", fontSize = 20.sp, color = TextColor)
            Spacer(modifier = Modifier.height(6.dp))
            Button(onClick = tryAgain, modifier = Modifier.size(120.dp, 53.dp)) {
                Text(text = "刷新", color = TextColor, fontSize = 18.sp)
            }
        }
    }
}


@Composable
fun DavItem(modifier: Modifier, fileFormat: FileFormat, name: String) {
    val image = when (fileFormat) {
        FileFormat.FLAC -> {
            painterResource(Res.drawable.flac)
        }

        FileFormat.MP3 -> {
            painterResource(Res.drawable.mp3)
        }

        FileFormat.UNKNOW -> {
            painterResource(Res.drawable.unknow)
        }

        else -> {
            painterResource(Res.drawable.folder)
        }
    }
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            modifier = Modifier.size(32.dp),
            painter = image, contentDescription = null,
            colorFilter = ColorFilter.tint(color = MaterialTheme.colors.primary)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = name,
            fontSize = 28.sp,
            color = TextColor,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CDLayout(
    modifier: Modifier, title: () -> String, isFocused: () -> Boolean, isPlaying: () -> Boolean
) {
    val titleWidth =
        animateDpAsState(targetValue = if (!isFocused()) 0.dp else 220.dp, label = "cd anim")
    ConstraintLayout(modifier = modifier) {
        val (name, cd) = createRefs()
        Box(
            modifier = Modifier
                .size(titleWidth.value, height = 48.dp)
                .constrainAs(name) {
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                    end.linkTo(cd.start)
                }
                .offset(x = 10.dp)
                .background(
                    color = Color(0xff263238),
                    shape = RoundedCornerShape(topStart = 20.dp, bottomStart = 20.dp)
                )
                .padding(
                    start = 20.dp, end = 10.dp
                )
        ) {
            Text(
                text = title(),
                fontSize = 21.sp,
                color = TextColor,
                modifier = Modifier
                    .basicMarquee()
                    .align(Alignment.Center),
                maxLines = 1,
                overflow = TextOverflow.Visible
            )
            Box(
                Modifier
                    .fillMaxHeight()
                    .width(50.dp)
                    .background(
                        brush = Brush.horizontalGradient(
                            listOf(
                                Color(0xff263238),
                                Color.Transparent
                            ),
                            startX = 10f

                        ),
                        shape = RoundedCornerShape(topStart = 20.dp, bottomStart = 20.dp)
                    )
                    .align(Alignment.CenterStart)
            ) {

            }
        }
        val anim = rememberInfiniteTransition(label = "cd rotation")
        val rotation by anim.animateFloat(
            initialValue = 0f,
            targetValue = 360f,
            animationSpec = infiniteRepeatable(
                animation = tween(8000, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            ), label = "cd rotoation"
        )
        Box(modifier = Modifier
            .background(
                shape = CircleShape,
                color = if (isFocused()) Color.White else Color.Transparent
            )
            .padding(3.dp)
            .size(96.dp)
            .constrainAs(cd) {
                top.linkTo(parent.top)
                end.linkTo(parent.end)
                bottom.linkTo(parent.bottom)
            }
            .rotate(if (isPlaying()) rotation else 0f)) {
            Image(
                painter = painterResource(Res.drawable.changpian_small), contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            Image(
                painter = painterResource(Res.drawable.launcher),
                contentDescription = null,
                modifier = Modifier
                    .size(36.dp)
                    .align(Alignment.Center)
                    .clip(CircleShape), contentScale = ContentScale.Crop
            )

        }

    }
}

@Preview
@Composable
fun CDLayoutPreview() {
    CDLayout(modifier = Modifier, { "素颜-许嵩" }, {
        true
    }) {
        true
    }
}
