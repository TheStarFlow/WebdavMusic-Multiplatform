package zzs.webdav.music.route


sealed class Screen(val route: String) {

    object HomeScreen : Screen("home_screen")


    object EmptyDavServerScreen : Screen("empty_server_screen")

    object LoadingScreen:Screen("loading")

    object PlayDetailScreen:Screen("play_detail_screen")

    //
    object DavDetailScreen : Screen("server_dav_screen")


    fun withArgs(vararg args: String): String {
        return buildString {
            append(route)
            args.forEach {
                append("/$it")
            }
        }
    }
}