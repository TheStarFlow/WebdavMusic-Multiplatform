package zzs.webdav.music.ui.dialog

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Checkbox
import androidx.compose.material.CheckboxDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.type
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import me.webdav.resources.Res
import me.webdav.resources.add_server
import zzs.webdav.music.theme.TextColor
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject
import zzs.webdav.music.bean.ServerDesc
import zzs.webdav.music.extension.collectAsState
import zzs.webdav.music.extension.focusClick
import zzs.webdav.music.ui.main.MainState
import zzs.webdav.music.ui.main.MainViewModel

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SettingDialog(
    editServer: (ServerDesc) -> Unit,
    addServer: () -> Unit,
    dismiss: () -> Unit,
    onDismiss: () -> Unit = {}
) {
    Dialog(onDismissRequest = onDismiss) {
        val mainViewModel: MainViewModel = koinInject<MainViewModel>()
        val serverDescList by mainViewModel.fetchCacheServerList()
            .collectAsState(initial = emptyList())
        val currServer by mainViewModel.collectAsState(prop1 = MainState::currServer)
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .size(400.dp, 480.dp)
                    .background(
                        color = MaterialTheme.colors.surface,
                        shape = RoundedCornerShape(10.dp)
                    )
                    .align(Alignment.Center)
                    .padding(top = 10.dp, bottom = 10.dp)
            ) {
                Text(
                    text = "设置", color = MaterialTheme.colors.primary,
                    fontSize = 36.sp,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
                Spacer(modifier = Modifier.height(10.dp))

                var focusedIndex by remember {
                    mutableIntStateOf(0)
                }
                val addFocusRequester = remember {
                    FocusRequester()
                }
                val listFocusRequester = remember {
                    FocusRequester()
                }
                LazyColumn(modifier = Modifier.focusRequester(listFocusRequester)) {
                    itemsIndexed(serverDescList) { index, item ->

                        var isFocused by remember {
                            mutableStateOf(false)
                        }
                        val focusRequester = remember {
                            FocusRequester()
                        }
                        var isWantDelete by remember {
                            mutableStateOf(false)
                        }
                        var isWantEdit by remember {
                            mutableStateOf(false)
                        }
                        var isCheck = item == currServer()

                        var scrollOffset = remember {
                            Offset(00f, 0f)
                        }
                        ServerItem(
                            modifier = Modifier
                                .focusProperties {
                                    up = if (index == 0) {
                                        FocusRequester.Cancel
                                    } else {
                                        FocusRequester.Default
                                    }
                                    left = FocusRequester.Cancel
                                    right = FocusRequester.Cancel
                                }
                                .focusClick(
                                    focusRequester = focusRequester,
                                    onKeyEvent = {
                                        if (it.type == KeyEventType.KeyDown
                                            || it.type == KeyEventType.KeyUp
                                        ) {
                                            if (it.key == Key.DirectionLeft) {
                                                if (it.type == KeyEventType.KeyDown) {
                                                    if (isWantEdit) {
                                                        isWantEdit = false
                                                    } else if (!isWantDelete) {
                                                        isWantDelete = !isCheck
                                                    }
                                                }
                                                focusRequester.requestFocus()
                                                true
                                            } else if (it.key == Key.DirectionRight) {
                                                if (it.type == KeyEventType.KeyDown) {
                                                    if (isWantDelete) {
                                                        isWantDelete = false
                                                    } else if (!isWantEdit) {
                                                        isWantEdit = true
                                                    }
                                                }
                                                focusRequester.requestFocus()
                                                true
                                                //想删除时其他按钮无效
                                            } else if (it.key == Key.DirectionUp && isWantDelete) {
                                                true
                                            } else if (it.key == Key.DirectionDown && isWantDelete) {
                                                true
                                            } else {
                                                false
                                            }
                                        } else
                                            false
                                    },
                                    onFocusChanged = {
                                        isFocused = it.isFocused
                                        focusedIndex = index
                                    }) {
                                    mainViewModel.setCurrServer(item)
                                    dismiss()
                                }
                                .pointerInput(Unit) {
                                    detectDragGestures(
                                        onDrag = { _, offset ->
                                            scrollOffset += offset
                                        },
                                        onDragEnd = {
                                            if (scrollOffset.x > 0) {
                                                if (isWantDelete) {
                                                    isWantDelete = false
                                                } else {
                                                    isWantEdit = true
                                                }
                                            } else {
                                                if (isWantEdit) {
                                                    isWantEdit = false
                                                } else {
                                                    isWantDelete = true
                                                }
                                            }
                                            focusRequester.requestFocus()
                                            scrollOffset = Offset.Zero
                                        }
                                    )
                                }
                                .fillMaxWidth()
                                .height(78.dp)
                                .background(color = if (isFocused or isWantDelete or isWantEdit) MaterialTheme.colors.primary else Color.Transparent),
                            item = item,
                            isCheck,
                            isFocused = isFocused || isWantDelete || isWantEdit,
                            //不能删除正式使用的服务器
                            isWantDelete && !isCheck,
                            isWantEdit = isWantEdit,
                            edit = editServer,
                        ) {
                            mainViewModel.deleteServer(item)
                            isWantDelete = false
                        }
                        if (item == currServer()) {
                            LaunchedEffect(key1 = Unit) {
                                focusRequester.requestFocus()
                            }
                        }
                        if (index == focusedIndex) {
                            LaunchedEffect(key1 = focusedIndex) {
                                if (focusedIndex == index) {
                                    focusRequester.requestFocus()
                                }
                            }
                        }
                    }

                }
                Spacer(modifier = Modifier.weight(1f))
                Row {
                    var addFocus by remember {
                        mutableStateOf(false)
                    }

                    var cancelFocus by remember {
                        mutableStateOf(false)
                    }
                    val cancelFocusRequester = remember {
                        FocusRequester()
                    }
                    MyFocusButton(modifier = Modifier
                        .weight(1f)
                        .height(53.dp)
                        .focusProperties {
                            // up = listFocusRequester
                            left = FocusRequester.Cancel
                            down = FocusRequester.Cancel
                            right = cancelFocusRequester
                        }
                        .focusClick(addFocusRequester, onFocusChanged = {
                            addFocus = it.isFocused
                        }, onClick = addServer),
                        text = "添加",
                        isFocused = addFocus,
                        radius = 0.dp
                    )
                    MyFocusButton(modifier = Modifier
                        .weight(1f)
                        .height(53.dp)
                        .focusProperties {
                            left = addFocusRequester
                            right = FocusRequester.Cancel
                            down = FocusRequester.Cancel
                        }
                        .focusClick(cancelFocusRequester, onFocusChanged = {
                            cancelFocus = it.isFocused
                        }) {
                            dismiss()
                        }, text = "关闭", isFocused = cancelFocus, radius = 0.dp
                    )
                }

            }
        }

    }
}

@Composable
fun ServerItem(
    modifier: Modifier,
    item: ServerDesc,
    isCheck: Boolean,
    isFocused: Boolean,
    isWantDelete: Boolean,
    isWantEdit: Boolean,
    edit: (ServerDesc) -> Unit,
    delete: () -> Unit = {}
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (isFocused) {
            val width by animateDpAsState(
                targetValue = if (isWantEdit) 96.dp else 0.dp,
                label = "edit width "
            )
            val editFocusRequester = remember {
                FocusRequester()
            }
            LaunchedEffect(key1 = isWantEdit) {
                if (isWantEdit) {
                    editFocusRequester.requestFocus()
                }
            }
            Box(
                modifier = Modifier
                    .width(width)
                    .fillMaxHeight()
                    .background(Color(0xffAAB8AB))
                    .focusClick(editFocusRequester, onClick = {
                        edit(item)
                    }),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (isWantEdit) "编辑" else "",
                    fontSize = 24.sp,
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Clip,
                    textAlign = TextAlign.Center
                )
            }
        }
        Spacer(modifier = Modifier.width(24.dp))
        Image(
            painter = painterResource(Res.drawable.add_server),
            contentDescription = null,
            modifier = Modifier.size(30.dp),
            colorFilter = ColorFilter.tint(
                color = if (isFocused) Color.White else MaterialTheme.colors.primary
            )
        )
        Spacer(modifier = Modifier.width(10.dp))
        Column(modifier = Modifier.padding(top = 10.dp, bottom = 10.dp)) {
            Text(
                text = item.name,
                fontSize = 28.sp,
                color = if (isFocused) TextColor else MaterialTheme.colors.primary
            )
            Text(
                text =  "${item.ip}:${item.port}",
                fontSize = 13.sp,
                color = if (isFocused) TextColor else MaterialTheme.colors.primary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.widthIn(max = 220.dp)
            )
        }
        Spacer(modifier = modifier.weight(1f))
        Checkbox(
            checked = isCheck,
            onCheckedChange = {},
            colors = if (isFocused) CheckboxDefaults.colors(
                checkmarkColor = MaterialTheme.colors.primary,
                checkedColor = Color.White
            ) else CheckboxDefaults.colors(
                checkedColor = MaterialTheme.colors.primary,
                checkmarkColor = Color.White,
            )
        )
        if (isFocused) {
            val width by animateDpAsState(
                targetValue = if (isWantDelete) 96.dp else 0.dp,
                label = "delete width "
            )
            val deleteFocus = remember {
                FocusRequester()
            }
            LaunchedEffect(key1 = isWantDelete) {
                if (isWantDelete) {
                    deleteFocus.requestFocus()
                }
            }
            Box(
                modifier = Modifier
                    .width(width)
                    .fillMaxHeight()
                    .background(Color.Red)
                    .focusClick(deleteFocus, onClick = delete),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (isWantDelete) "删除" else "",
                    fontSize = 24.sp,
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Clip,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}