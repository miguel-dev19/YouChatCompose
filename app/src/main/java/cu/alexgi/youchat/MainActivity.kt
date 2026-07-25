package cu.alexgi.youchat

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import cu.alexgi.youchat.core.theme.YouChatTheme
import cu.alexgi.youchat.core.navigation.YouChatNavGraph
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            YouChatTheme {
                YouChatNavGraph()
            }
        }
    }
}
