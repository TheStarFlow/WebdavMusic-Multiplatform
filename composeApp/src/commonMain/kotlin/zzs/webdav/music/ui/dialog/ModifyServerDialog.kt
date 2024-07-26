package zzs.webdav.music.ui.dialog

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import zzs.webdav.music.bean.ServerDesc
import zzs.webdav.music.bean.isValidate
import zzs.webdav.music.extension.focusClick
import zzs.webdav.music.theme.TextColor

/**
 * @param ServerDesc
 * @param Boolean  true 插入 false 更新
 * */
typealias ModifyServer = (ServerDesc, Boolean) -> Unit


@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ModifyServerDialog(
    modifyServer: ModifyServer,
    currServer: ServerDesc? = null,
    dismiss: () -> Unit = {}
) {
    Dialog(
        onDismissRequest = {

        }
    ) {
        val name = remember { mutableStateOf(currServer?.name ?: "") }
        val ip = remember { mutableStateOf(currServer?.ip ?: "") }
        val port = remember { mutableStateOf(currServer?.port ?: "") }
        val user = remember { mutableStateOf(currServer?.user ?: "") }
        val password = remember { mutableStateOf(currServer?.password ?: "") }
        val targetPath = remember { mutableStateOf(currServer?.targetPath ?: "/dav") }
        Box(modifier = Modifier.fillMaxSize().background(color = Color.Transparent)) {
            Surface(
                modifier = Modifier
                    .size(600.dp, 340.dp)
                    .align(Alignment.Center),
                shape = RoundedCornerShape(7.dp),
                color = MaterialTheme.colors.background
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(start = 24.dp, end = 24.dp, top = 12.dp, bottom = 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = if (currServer == null) "添加" else "修改" + "服务器",
                        style = MaterialTheme.typography.h4,
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(10.dp))
                    val (nameF, ipF, portF, userF, passwordF, pathF) = remember {
                        FocusRequester.createRefs()
                    }
                    LaunchedEffect(key1 = Unit) {
                        nameF.requestFocus()
                    }
                    val focusManager = LocalFocusManager.current
                    Row {
                        Item(
                            title = "名称",
                            value = name,
                            modifier = Modifier.focusRequester(nameF)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Item(
                            title = "IP",
                            value = ip,
                            modifier = Modifier
                        )
                    }
                    Row {
                        Item(
                            title = "端口", value = port, modifier = Modifier

                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Item(
                            title = "用户", value = user, modifier = Modifier

                        )
                    }
                    Row {
                        Item(
                            title = "密码", value = password, modifier = Modifier

                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Item(
                            title = "路径（可选）", value = targetPath, modifier = Modifier

                        )
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    Row {
                        val (confirm, cancel) = remember {
                            FocusRequester.createRefs()
                        }
                        var isConfirmFocused by remember {
                            mutableStateOf(false)
                        }
                        var isCancelFocused by remember {
                            mutableStateOf(false)
                        }
                        MyFocusButton(modifier = Modifier
                            .padding(start = 8.dp, end = 8.dp, top = 4.dp, bottom = 4.dp)
                            .size(142.dp, 53.dp)
                            .focusProperties {
                                right = cancel
                            }
                            .focusClick(
                                focusRequester = confirm, onFocusChanged = {
                                    isConfirmFocused = it.isFocused
                                }
                            ) {
                                val server = ServerDesc(
                                    name = name.value.trim(),
                                    ip = ip.value.trim(),
                                    port = port.value.trim(),
                                    user = user.value.trim(),
                                    password = password.value.trim(),
                                    targetPath = targetPath.value.trim()
                                )
                                if (!server.isValidate()) {
                                    return@focusClick
                                }
                                if (currServer != null) {

                                }
                                modifyServer(server, currServer == null)
                                dismiss()
                            }, text = "确定", isFocused = isConfirmFocused
                        )
                        Spacer(modifier = Modifier.width(72.dp))
                        MyFocusButton(modifier = Modifier
                            .padding(start = 8.dp, end = 8.dp, top = 4.dp, bottom = 4.dp)
                            .size(142.dp, 53.dp)
                            .focusProperties {
                                left = confirm
                            }
                            .focusClick(
                                focusRequester = cancel, onFocusChanged = {
                                    isCancelFocused = it.isFocused
                                }
                            ) {
                                dismiss()
                            }, text = "取消", isFocused = isCancelFocused
                        )
                    }
                }
            }
//                if (context.isDebuggable()) {
//                    LaunchedEffect(key1 = Unit) {
//                        delay(100)
//                        addNewServer(DEFAULT_SERVER)
//                        dismiss()
//                    }
//                    MockKey(modifier = Modifier.align(Alignment.BottomEnd))
//                }
        }
    }
}


@Composable
fun MyFocusButton(
    modifier: Modifier,
    text: String,
    isFocused: Boolean,
    fontSize: TextUnit = TextUnit.Unspecified,
    defaultColor:Color = Color.Transparent,
    radius: Dp = 27.dp
) {
    Box(
        modifier = modifier.background(
            if (isFocused) MaterialTheme.colors.primary else defaultColor,
            shape = RoundedCornerShape(radius)
        ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontSize = fontSize,
            color = if (isFocused) TextColor else MaterialTheme.colors.primary,
        )
    }

}

@Composable
fun Item(
    title: String, value: MutableState<String>, modifier: Modifier
) {
    OutlinedTextField(
        value = value.value,
        onValueChange = {
            value.value = it
        },
        modifier = modifier,
        label = { Text(text = title) },
    )
}