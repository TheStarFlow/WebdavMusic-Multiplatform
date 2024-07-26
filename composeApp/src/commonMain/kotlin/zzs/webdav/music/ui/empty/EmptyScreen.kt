package zzs.webdav.music.ui.empty

import androidx.compose.foundation.Image
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
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.painterResource
import me.webdav.resources.Res
import me.webdav.resources.add_server
import zzs.webdav.music.extension.focusClick
import zzs.webdav.music.ui.dialog.ModifyServer
import zzs.webdav.music.ui.dialog.ModifyServerDialog

@Composable
fun EmptyScreen(
    modifyServer: ModifyServer,
) {
    var showDialog by remember { mutableStateOf(false) }
    Box(
        modifier = Modifier.fillMaxSize()
            .background(color = MaterialTheme.colors.background)
    ) {
        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "啥也没有~", fontSize = 26.sp,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(18.dp))
            val focusRequester = remember {
                FocusRequester()
            }
            LaunchedEffect(key1 = Unit) {
                focusRequester.requestFocus()
            }
            var isFocused by remember {
                mutableStateOf(false)
            }
            Box(modifier = Modifier
                    .focusClick(focusRequester = focusRequester, onFocusChanged = {
                        isFocused = it.isFocused
                    }) {
                        showDialog = true
                    }.background(
                        color = if (isFocused) MaterialTheme.colors.primary else Color.White,
                        shape = RoundedCornerShape(6.dp)
                    )) {
                Row(
                    modifier = Modifier.padding(
                        start = 10.dp,
                        end = 10.dp,
                        top = 6.dp,
                        bottom = 6.dp
                    ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "添加服务器",
                        fontSize = 26.sp,
                        color = if (isFocused) Color.White else MaterialTheme.colors.primary
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    //引入这个包下的函数  org.jetbrains.compose.resources.painterResource
                    Image(
                        modifier = Modifier.size(32.dp),
                        painter = painterResource(Res.drawable.add_server),
                        contentDescription = null,
                        colorFilter = ColorFilter.tint(if (isFocused) Color.White else MaterialTheme.colors.primary)
                    )
                }
            }

        }
    }
    if (showDialog) {
        ModifyServerDialog(modifyServer = modifyServer) {
            showDialog = false
        }
    }
}