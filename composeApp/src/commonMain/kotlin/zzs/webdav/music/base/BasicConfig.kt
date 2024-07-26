package zzs.webdav.music.base

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density

@Composable
fun BasicCompose(
    width:Float,
    height:Float,
    dpWidth: Float = 1280.0f,
    dpHeight: Float = 720.0f,
    useWidth: Boolean = true,
    content: @Composable () -> Unit
) {
    val targetDensityWidth = width / dpWidth
    val targetDensityHeight = height / dpHeight
    CompositionLocalProvider(
        LocalDensity provides Density(
            density = if (useWidth) targetDensityWidth else targetDensityHeight
        ),
    ) {
        content()
    }
}