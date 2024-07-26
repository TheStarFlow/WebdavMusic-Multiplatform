//package zzs.webdav.music.ui.playlist
//
//import androidx.compose.animation.core.Animatable
//import androidx.compose.animation.core.animateFloatAsState
//import androidx.compose.foundation.BorderStroke
//import androidx.compose.foundation.Image
//import androidx.compose.foundation.LocalIndication
//import androidx.compose.foundation.background
//import androidx.compose.foundation.clickable
//import androidx.compose.foundation.layout.Arrangement
//import androidx.compose.foundation.layout.Box
//import androidx.compose.foundation.layout.Row
//import androidx.compose.foundation.layout.Spacer
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.height
//import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.layout.size
//import androidx.compose.foundation.layout.width
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.foundation.lazy.itemsIndexed
//import androidx.compose.foundation.lazy.rememberLazyListState
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.LaunchedEffect
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.mutableIntStateOf
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.remember
//import androidx.compose.runtime.rememberCoroutineScope
//import androidx.compose.runtime.setValue
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.ExperimentalComposeUiApi
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.focus.FocusDirection
//import androidx.compose.ui.focus.FocusRequester
//import androidx.compose.ui.focus.focusProperties
//import androidx.compose.ui.focus.focusTarget
//import androidx.compose.ui.graphics.Brush
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.graphics.ColorFilter
//import androidx.compose.ui.graphics.graphicsLayer
//import androidx.compose.ui.graphics.vector.ImageVector
//import androidx.compose.ui.input.key.Key
//import androidx.compose.ui.input.key.KeyEventType
//import androidx.compose.ui.input.key.key
//import androidx.compose.ui.input.key.type
//import androidx.compose.ui.platform.LocalDensity
//import androidx.compose.ui.text.SpanStyle
//import androidx.compose.ui.text.buildAnnotatedString
//import androidx.compose.ui.text.style.TextAlign
//import androidx.compose.ui.text.style.TextOverflow
//import androidx.compose.ui.text.withStyle
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import androidx.constraintlayout.compose.ConstrainedLayoutReference
//import androidx.constraintlayout.compose.ConstraintLayout
//import androidx.constraintlayout.compose.ConstraintLayoutScope
//import androidx.constraintlayout.compose.Dimension
//import kotlinx.coroutines.launch
//import org.koin.compose.LocalKoinApplication
//import org.koin.compose.koinInject
//import zzs.webdav.music.extension.collectAsState
//import zzs.webdav.music.ui.playing.PlayingUIState
//import zzs.webdav.music.ui.playing.PlayingViewModel
//
//
//@OptIn(ExperimentalComposeUiApi::class)
//@Composable
//fun PlayList(onBack: () -> Unit) {
//    val playingViewModel: PlayingViewModel = koinInject()
//    val playList by playingViewModel.collectAsState(PlayingUIState::playList)
//    val isPlaying by playingViewModel.collectAsState(prop1 = PlayingUIState::isPlaying)
//    val playPosition by playingViewModel.collectAsState(prop1 = PlayingUIState::playListPosition)
//    val screenWidth  = Local
//    val transX = remember {
//        Animatable(screenWidth.toFloat())
//    }
//    val scope = rememberCoroutineScope()
//    LaunchedEffect(key1 = Unit) {
//        transX.animateTo(0f)
//    }
//    val backInvoke = {
//        scope.launch {
//            transX.animateTo(screenWidth.toFloat()) {
//                if (value == screenWidth.toFloat()) {
//                    onBack()
//                }
//            }
//        }
//    }
//    BackHandler {
//        backInvoke()
//    }
//    ConstraintLayout(
//        modifier = modifier
//            .graphicsLayer {
//                translationX = transX.value
//            }
//            .fillMaxSize()
//            .background(
//                brush =
//                Brush.horizontalGradient(
//                    listOf(
//                        Color.Transparent,
//                        MaterialTheme.colorScheme.background.copy(alpha = 0.1f),
//                        MaterialTheme.colorScheme.background.copy(alpha = 0.2f),
//                        MaterialTheme.colorScheme.background.copy(alpha = 0.3f),
//                        MaterialTheme.colorScheme.background.copy(alpha = 0.4f),
//                        MaterialTheme.colorScheme.background.copy(alpha = 0.5f),
//                        MaterialTheme.colorScheme.background.copy(alpha = 0.6f),
//                        MaterialTheme.colorScheme.background.copy(alpha = 0.7f),
//                        MaterialTheme.colorScheme.background.copy(alpha = 0.8f),
//                        MaterialTheme.colorScheme.background.copy(alpha = 0.8f),
//                        MaterialTheme.colorScheme.background.copy(0.9f),
//                        MaterialTheme.colorScheme.background.copy(0.9f),
//                        MaterialTheme.colorScheme.background.copy(0.9f),
//                        MaterialTheme.colorScheme.background,
//                        MaterialTheme.colorScheme.background
//                    ),
//                )
//            )
//            .focusProperties {
//                left = FocusRequester.Cancel
//                right = FocusRequester.Cancel
//                exit = {
//                    if (it == FocusDirection.Down
//                        || it == FocusDirection.Up
//                    ) {
//                        FocusRequester.Cancel
//                    } else
//                        FocusRequester.Default
//                }
//            }
//            .focusTarget()
//            .clickable {
//                backInvoke()
//            }
//    ) {
//        val (title, space, items, order) = createRefs()
//        PlayListTitle(title = title) {
//            playList.size
//        }
//        PlayMode(order = order, title = title)
//        Divider(space = space, title = title)
//        var focusedIndex by remember {
//            mutableIntStateOf(playPosition)
//        }
//        val state = rememberLazyListState()
//        LazyColumn(
//            state = state,
//            modifier = modifier
//                .constrainAs(items) {
//                    start.linkTo(title.start)
//                    top.linkTo(space.bottom, 12.dp)
//                    bottom.linkTo(parent.bottom, 24.dp)
//                    end.linkTo(parent.end, 24.dp)
//                    height = Dimension.fillToConstraints
//                    width = Dimension.fillToConstraints
//                }
//                .padding(end = 40.dp),
//            verticalArrangement = Arrangement.spacedBy(8.dp),
//        ) {
//            itemsIndexed(playList) { index, item ->
//
//                val itemFocusRequester = remember {
//                    FocusRequester()
//                }
//                var itemFocused by remember {
//                    mutableStateOf(false)
//                }
//                val scale = animateFloatAsState(
//                    targetValue = if (itemFocused) 1.1f else 1f,
//                    label = "playListAnim"
//                )
//                PlayListItem(
//                    modifier = modifier
//                        .fillMaxWidth()
//                        .padding(top = 2.dp, bottom = 2.dp)
//                        .focusProperties {
//                            left = FocusRequester.Cancel
//                            right = FocusRequester.Cancel
//                            exit = {
//                                if (it == FocusDirection.Down
//                                    || it == FocusDirection.Up
//                                ) {
//                                    FocusRequester.Cancel
//                                } else
//                                    FocusRequester.Default
//                            }
//                        }
//                        .focusClick(itemFocusRequester, onFocusChanged = {
//                            itemFocused = it.isFocused
//                            if (itemFocused) {
//                                focusedIndex = index
//                            }
//                        }, onKeyEvent = {
//                            if (it.key == Key.Back && it.type == KeyEventType.KeyDown) {
//                                backInvoke()
//                                true
//                            } else {
//                                false
//                            }
//                        }) {
//                            playingViewModel.playItem(index)
//                        }
//                        .graphicsLayer {
//                            scaleX = scale.value
//                            scaleY = scale.value
//                        },
//                    isPlaying = {
//                        isPlaying && index == playPosition
//                    },
//                    index = index + 1,
//                    name = item.fetchSingName() ?: "",
//                    singer = item.fetchSingerName() ?: "",
//                    isFocused = {
//                        itemFocused
//                    }
//                )
//                if (focusedIndex == index) {
//                    LaunchedEffect(key1 = focusedIndex) {
//                        itemFocusRequester.requestFocus()
//                    }
//                }
//            }
//        }
//        var scrollerSwitch by remember {
//            mutableStateOf(true)
//        }
//        LaunchedEffect(key1 = Unit) {
//            state.scrollToItem(playPosition)
//            scrollerSwitch = false
//        }
//        if (!scrollerSwitch) {
//            LaunchedEffect(key1 = focusedIndex) {
//                state.animateScrollToItem(focusedIndex, VerticalScrollBehaviour)
//            }
//            LaunchedEffect(key1 = playPosition) {
//                state.animateScrollToItem(playPosition)
//            }
//        }
//    }
//
//}
//
//@Composable
//fun PlayListItem(
//    modifier: Modifier,
//    isPlaying: () -> Boolean,
//    index: Int,
//    name: String,
//    singer: String,
//    isFocused: ()->Boolean
//) {
//    Surface(
//        color = Color.Transparent,
//        modifier = modifier,
//        shape = RoundedCornerShape(10.dp),
//        border = if (isFocused()) BorderStroke(2.dp, Color.White) else null
//    ) {
//
//        Row(
//            verticalAlignment = Alignment.CenterVertically,
//        ) {
//            Box(
//                contentAlignment = Alignment.Center,
//                modifier = Modifier.size(40.dp)
//            ) {
//                if (isPlaying()) {
//                    Image(
//                        painter = coliImagePainter(R.mipmap.ic_sound_wave_orange),
//                        contentDescription = null,
//                        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary)
//                    )
//                } else {
//                    Text(text = "$index", fontSize = 16.sp, color = TextColor.copy(0.5f))
//                }
//            }
//            Spacer(modifier = Modifier.width(16.dp))
//            Text(
//                text = buildAnnotatedString {
//                    append(name)
//                    withStyle(
//                        style = SpanStyle(
//                            color = TextColor.copy(0.3f),
//                            fontSize = 20.sp
//                        )
//                    ) {
//                        append(" - ")
//                        append(singer)
//                    }
//                },
//                fontSize = 21.sp, color = TextColor,
//                overflow = TextOverflow.Ellipsis,
//                maxLines = 1,
//            )
//        }
//    }
//}
//
//@Composable
//internal fun ConstraintLayoutScope.PlayListTitle(
//    title: ConstrainedLayoutReference,
//    size: () -> Int
//) {
//    Text(modifier = Modifier.constrainAs(title) {
//        top.linkTo(parent.top, 20.dp)
//        end.linkTo(parent.end, 240.dp)
//    }, text = buildAnnotatedString {
//        append("当前播放 ")
//        withStyle(
//            style = SpanStyle(
//                fontSize = 20.sp,
//                color = TextColor.copy(0.5f)
//            )
//        ) {
//            append("(${size()})")
//        }
//    }, fontSize = 32.sp, color = TextColor, textAlign = TextAlign.Center)
//}
//
//
//@Composable
//internal fun ConstraintLayoutScope.Divider(
//    space: ConstrainedLayoutReference, title: ConstrainedLayoutReference
//) {
//    Box(
//        modifier = Modifier
//            .constrainAs(space) {
//                top.linkTo(title.bottom, 12.dp)
//                start.linkTo(title.start)
//                end.linkTo(parent.end, 40.dp)
//                width = Dimension.fillToConstraints
//            }
//            .height(height = 1.dp)
//            .background(color = MaterialTheme.colorScheme.background.copy(0.1f))
//    )
//}
//
//
//@OptIn(ExperimentalComposeUiApi::class)
//@Composable
//internal fun ConstraintLayoutScope.PlayMode(
//    order: ConstrainedLayoutReference,
//    title: ConstrainedLayoutReference
//) {
//    val playingViewModel: PlayingViewModel = mavericksActivityViewModel()
//    val playMode by playingViewModel.collectAsState(prop1 = PlayingUIState::playMode)
//    val resId = when (playMode) {
//        0 -> R.drawable.list_order
//        1 -> R.drawable.list_loop
//        2 -> R.drawable.single_loop
//        else -> {
//            R.drawable.list_random
//        }
//    }
//    val focusRequester = remember {
//        FocusRequester()
//    }
//    var isFocused by remember {
//        mutableStateOf(false)
//    }
//    Surface(
//        modifier = Modifier
//            .constrainAs(order) {
//                bottom.linkTo(title.bottom)
//                end.linkTo(parent.end, 40.dp)
//            }
//            .focusProperties {
//                left = FocusRequester.Cancel
//                right = FocusRequester.Cancel
//                exit = {
//                    if (it == FocusDirection.Down
//                        || it == FocusDirection.Up
//                    ) {
//                        FocusRequester.Cancel
//                    } else
//                        FocusRequester.Default
//                }
//            }
//            .focusClick(
//                focusRequester = focusRequester, onFocusChanged = {
//                    isFocused = it.isFocused
//                }) {
//                playingViewModel.setPlayMode()
//            },
//        color = Color.Transparent,
//        shape = RoundedCornerShape(10.dp),
//    ) {
//        val colorAlpha = if (isFocused) 1.0f else 0.5f
//        Row(
//            verticalAlignment = Alignment.CenterVertically
//        ) {
//            Image(
//                imageVector = ImageVector.vectorResource(resId), contentDescription = null,
//                modifier = Modifier.size(20.dp),
//                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary.copy(alpha = colorAlpha))
//            )
//            Spacer(modifier = Modifier.width(4.dp))
//            Text(
//                text = PlayMode.values()[playMode].modeName,
//                fontSize = 20.sp, color = TextColor.copy(alpha = colorAlpha),
//            )
//        }
//    }
//
//}