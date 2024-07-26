package zzs.webdav.music.bean

import org.jetbrains.compose.resources.DrawableResource

data class MenuItem(
    val drawableId: DrawableResource,
    val name: String,
    val enable: Boolean = true,
    val call: () -> Unit
)
