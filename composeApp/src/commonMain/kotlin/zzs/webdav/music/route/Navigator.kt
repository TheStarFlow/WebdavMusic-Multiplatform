package zzs.webdav.music.route

import androidx.navigation.NavHostController
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.compose.DialogNavigator
import androidx.navigation.navOptions

interface AppNavigator {
    fun push(router: Screen, isSingleTop: Boolean = false) = push(router.route, isSingleTop)
    fun push(route: String, isSingleTop: Boolean = false): Boolean
    fun pop(): Boolean

    val navController: NavHostController
}


class AppNavigatorImpl() : AppNavigator {

    override val navController = NavHostController().apply {
        navigatorProvider.addNavigator(ComposeNavigator())
        navigatorProvider.addNavigator(DialogNavigator())
    }

    override fun push(route: String, isSingleTop: Boolean): Boolean {
        navController.navigate(
            route,
            navOptions {
                launchSingleTop = isSingleTop
            }
        )
        return true
    }

    override fun pop(): Boolean {
        return  navController.navigateUp()
    }
}