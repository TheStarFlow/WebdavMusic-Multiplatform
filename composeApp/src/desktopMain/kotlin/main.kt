import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import org.koin.core.Koin
import zzs.webdav.music.di.initKoin
import zzs.webdav.music.ui.main.MainScreen

lateinit var koin: Koin

fun main() {
    koin = initKoin().koin
    return application {
        Window(
            onCloseRequest = ::exitApplication,
            title = "WebdavMusic-Multiplatform",
            state = rememberWindowState(
                position = WindowPosition.Aligned(Alignment.Center),
                size = DpSize(960.dp,540.dp)
            )
        ) {
            MainScreen(showBack = true){
                exitApplication()
            }
        }
    }
}