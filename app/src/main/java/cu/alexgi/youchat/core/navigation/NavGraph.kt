package cu.alexgi.youchat.core.navigation

import androidx.compose.runtime.*
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import cu.alexgi.youchat.ui.login.LoginScreen
import cu.alexgi.youchat.ui.welcome.WelcomeScreen
import cu.alexgi.youchat.ui.home.HomeScreen
import cu.alexgi.youchat.ui.contacts.ContactsScreen
import cu.alexgi.youchat.ui.chat.ChatScreen
import java.net.URLDecoder
import java.net.URLEncoder

object Routes {
    const val LOGIN = "login"
    const val WELCOME = "welcome"
    const val HOME = "home"
    const val CONTACTS = "contacts"
    const val CHAT = "chat/{nombre}/{correo}"
    
    fun chatRoute(nombre: String, correo: String): String {
        val encodedNombre = URLEncoder.encode(nombre, "UTF-8")
        val encodedCorreo = URLEncoder.encode(correo, "UTF-8")
        return "chat/$encodedNombre/$encodedCorreo"
    }
}

@Composable
fun YouChatNavGraph() {
    val navController = rememberNavController()
    
    NavHost(navController = navController, startDestination = Routes.LOGIN) {
        composable(Routes.LOGIN) {
            LoginScreen(onLoginSuccess = {
                navController.navigate(Routes.WELCOME) {
                    popUpTo(Routes.LOGIN) { inclusive = true }
                }
            })
        }
        composable(Routes.WELCOME) {
            WelcomeScreen(onFinish = {
                navController.navigate(Routes.HOME) {
                    popUpTo(Routes.WELCOME) { inclusive = true }
                }
            })
        }
        composable(Routes.HOME) {
            HomeScreen(
                onChatClick = { nombre, correo ->
                    navController.navigate(Routes.chatRoute(nombre, correo))
                },
                onContactsClick = { navController.navigate(Routes.CONTACTS) }
            )
        }
        composable(Routes.CONTACTS) {
            ContactsScreen(
                onBack = { navController.popBackStack() },
                onChatClick = { nombre, correo ->
                    navController.navigate(Routes.chatRoute(nombre, correo))
                }
            )
        }
        composable(
            route = Routes.CHAT,
            arguments = listOf(
                navArgument("nombre") { type = NavType.StringType },
                navArgument("correo") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val nombre = URLDecoder.decode(backStackEntry.arguments?.getString("nombre") ?: "", "UTF-8")
            val correo = URLDecoder.decode(backStackEntry.arguments?.getString("correo") ?: "", "UTF-8")
            
            ChatScreen(
                nombre = nombre,
                correo = correo,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
