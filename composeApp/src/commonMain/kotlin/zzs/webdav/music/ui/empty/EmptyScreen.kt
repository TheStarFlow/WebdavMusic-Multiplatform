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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import webdavmusic_multiplatform.composeapp.generated.resources.Res
import webdavmusic_multiplatform.composeapp.generated.resources.compose_multiplatform
import zzs.webdav.music.extension.focusClick
import zzs.webdav.music.ui.modifyserver.ModifyServer
import zzs.webdav.music.ui.modifyserver.ModifyServerDialog

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
                text = "啥也没有~", style = MaterialTheme.typography.h4,
                color = Color.LightGray
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
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = if (isFocused) Color.White else Color.Transparent,
                modifier = Modifier
                    .focusClick(focusRequester = focusRequester, onFocusChanged = {
                        isFocused = it.isFocused
                    }) {
                        showDialog = true
                    }
            ) {
                Row(
                    modifier = Modifier.padding(
                        start = 8.dp,
                        end = 8.dp,
                        top = 4.dp,
                        bottom = 4.dp
                    ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "添加服务器",
                        style = MaterialTheme.typography.h2,
                        color = MaterialTheme.colors.primary
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    // Image(painterResource(Res.drawable.compose_multiplatform.toString()), null)
//                    Image(
//                        modifier = Modifier.size(24.dp),
//                        painter = painterResource(Res.drawable.compose_multiplatform),
//                        contentDescription = null,
//                        colorFilter = ColorFilter.tint(MaterialTheme.colors.primary)
//                    )
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