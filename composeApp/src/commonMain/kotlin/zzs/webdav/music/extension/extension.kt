package zzs.webdav.music.extension

import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.FocusState
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type


fun Modifier.focusClick(
    focusRequester: FocusRequester,
    enable: () -> Boolean = { true },
    onKeyEvent: (KeyEvent) -> Boolean = { false },
    onFocusChanged: (FocusState) -> Unit = {},
    onClick: () -> Unit = {},
) = this.then(
    this
        .onKeyEvent {
            if (it.type == KeyEventType.KeyDown||it.type==KeyEventType.KeyUp) {
                if (it.key == Key.Enter || Key.DirectionCenter == it.key) {
                    if (it.type == KeyEventType.KeyDown) {
                        onClick()
                    }
                    true
                } else {
                    onKeyEvent(it)
                }
            } else {
                onKeyEvent(it)
            }
        }
        .focusRequester(focusRequester)
        .onFocusChanged(onFocusChanged)
        .focusable()
        .clickable(enabled = enable(), onClick = {
            focusRequester.requestFocus()
            onClick()
        })
)

fun Modifier.handleBack(invoke: () -> Unit) = this.then(
    onKeyEvent {
        if (it.type == KeyEventType.KeyDown
            && it.key == Key.Back
        ) {
            invoke()
            return@onKeyEvent true
        }
        false
    }
)


fun Modifier.focusMove(
    focusManager: FocusManager, focusRequester: FocusRequester
) = this.then(
    onKeyEvent {
        val direction = getFocusDirection(it)
        direction?.run {
            focusManager.moveFocus(direction)
        }
        false
    }.focusRequester(focusRequester)
)

//@Composable
//fun coliImagePainter(id: Int): Painter {
//    val context = LocalContext.current
//    val imgLoader = ImageLoader.Builder(context)
//        .components {
//            if (SDK_INT >= 28) {
//                add(ImageDecoderDecoder.Factory())
//            } else {
//                add(GifDecoder.Factory())
//            }
//        }
//        .build()
//
//    return rememberAsyncImagePainter(id, imgLoader)
//}


