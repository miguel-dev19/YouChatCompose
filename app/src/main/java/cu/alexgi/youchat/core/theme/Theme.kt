package cu.alexgi.youchat.core.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val YouChatBlue = Color(0xFF3F51B5)
val YouChatBlueDark = Color(0xFF303F9F)
val YouChatBackground = Color(0xFFEDF2F8)

private val LightColorScheme = lightColorScheme(
    primary = YouChatBlue,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFC5CAE9),
    secondary = Color(0xFF03A9F4),
    background = YouChatBackground,
    surface = Color.White,
    error = Color(0xFFE53935)
)

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF7986CB),
    onPrimary = Color.White,
    primaryContainer = YouChatBlueDark,
    secondary = Color(0xFF4FC3F7),
    background = Color(0xFF121212),
    surface = Color(0xFF1E1E1E),
    error = Color(0xFFEF5350)
)

@Composable
fun YouChatTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography(),
        content = content
    )
}
