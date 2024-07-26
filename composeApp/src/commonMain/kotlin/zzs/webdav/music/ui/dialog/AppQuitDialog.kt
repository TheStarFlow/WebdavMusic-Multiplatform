package zzs.webdav.music.ui.dialog


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.focusGroup
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.constraintlayout.compose.ConstraintLayout


import kotlinx.coroutines.delay
import zzs.webdav.music.extension.focusClick
import zzs.webdav.music.theme.TextColor


@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun AppQuitDialog(quit: () -> Unit, dismiss: () -> Unit,onDismiss:()->Unit = {}) {
    Dialog(
        onDismissRequest = onDismiss
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .focusGroup()
        ) {
            Box(
                modifier = Modifier
                    .size(560.dp, 360.dp)
                    .background(
                        color = MaterialTheme.colors.surface,
                        shape = RoundedCornerShape(20.dp)
                    )
                    .align(Alignment.Center)
                    .padding(top = 10.dp, bottom = 10.dp)
            ) {
                Text(
                    text = "确认退出Webdav音乐", color = TextColor,
                    fontSize = 30.sp,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .offset(y = -(50).dp)
                )

                Row(
                    modifier = Modifier.align(Alignment.BottomCenter)
                        .padding(start = 40.dp, end = 40.dp, bottom = 24.dp)
                ) {
                    val (confirm, cancel) = remember {
                        FocusRequester.createRefs()
                    }
                    var isConfirmFocused by remember {
                        mutableStateOf(false)
                    }
                    var isCancelFocused by remember {
                        mutableStateOf(false)
                    }
                    val manager = LocalFocusManager.current
                    LaunchedEffect(key1 = Unit) {
                        manager.clearFocus()
                        delay(10)
                        confirm.requestFocus()
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    MyFocusButton(modifier = Modifier
                        .padding(start = 8.dp, end = 8.dp, top = 4.dp, bottom = 4.dp)
                        .size(142.dp, 53.dp)
                        .focusClick(
                            focusRequester = confirm, onFocusChanged = {
                                isConfirmFocused = it.isFocused
                            }
                        ) {
                            quit.invoke()
                            dismiss()
                        }, text = "确定", isFocused = isConfirmFocused, fontSize = 24.sp
                    )
                    Spacer(modifier = Modifier.width(72.dp))
                    MyFocusButton(modifier = Modifier
                        .padding(start = 8.dp, end = 8.dp, top = 4.dp, bottom = 4.dp)
                        .size(142.dp, 53.dp)
                        .focusClick(
                            focusRequester = cancel, onFocusChanged = {
                                isCancelFocused = it.isFocused
                            }
                        ) {
                            dismiss()
                        }, text = "取消", isFocused = isCancelFocused, 24.sp
                    )
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

