package cu.alexgi.youchat.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "memoria")

data class UserPreferences(
    val alias: String = "", val correo: String = "", val pass: String = "",
    val mark: Int = 1, val temaApp: Int = 0,
    val lectura: Boolean = true, val notificacion: Boolean = true,
    val sonido: Boolean = true, val chatSecurity: Boolean = true,
    val actualizarPerfil: Boolean = true, val estadoPersonal: Boolean = true,
    val descargaAutMultimediaChat: Boolean = false, val tamMaxDescargaChat: Long = 128,
    val calidad: Int = 20, val ordenContactoNombre: Boolean = true,
    val enviarEnter: Boolean = false
)

@Singleton
class YouChatPreferences @Inject constructor(@ApplicationContext private val context: Context) {
    private object Keys {
        val ALIAS = stringPreferencesKey("alias")
        val CORREO = stringPreferencesKey("correo")
        val PASS = stringPreferencesKey("pass")
        val MARK = intPreferencesKey("mark")
        val TEMA_APP = intPreferencesKey("temaApp")
        val LECTURA = booleanPreferencesKey("lectura")
        val NOTIFICACION = booleanPreferencesKey("notificacion")
        val SONIDO = booleanPreferencesKey("sonido")
        val CHAT_SECURITY = booleanPreferencesKey("chat_security")
        val CALIDAD = intPreferencesKey("calidad")
    }

    val preferences: Flow<UserPreferences> = context.dataStore.data.map { prefs ->
        UserPreferences(
            alias = prefs[Keys.ALIAS] ?: "", correo = prefs[Keys.CORREO] ?: "",
            pass = prefs[Keys.PASS] ?: "", mark = prefs[Keys.MARK] ?: 1,
            temaApp = prefs[Keys.TEMA_APP] ?: 0,
            lectura = prefs[Keys.LECTURA] ?: true, notificacion = prefs[Keys.NOTIFICACION] ?: true,
            sonido = prefs[Keys.SONIDO] ?: true, chatSecurity = prefs[Keys.CHAT_SECURITY] ?: true,
            calidad = prefs[Keys.CALIDAD] ?: 20
        )
    }

    suspend fun setAlias(alias: String) { context.dataStore.edit { it[Keys.ALIAS] = alias } }
    suspend fun setCredentials(correo: String, pass: String) {
        context.dataStore.edit { it[Keys.CORREO] = correo; it[Keys.PASS] = pass; it[Keys.MARK] = 3 }
    }
    suspend fun setMark(mark: Int) { context.dataStore.edit { it[Keys.MARK] = mark } }
    suspend fun setTemaApp(tema: Int) { context.dataStore.edit { it[Keys.TEMA_APP] = tema } }
    suspend fun setLectura(l: Boolean) { context.dataStore.edit { it[Keys.LECTURA] = l } }
    suspend fun setNotificacion(n: Boolean) { context.dataStore.edit { it[Keys.NOTIFICACION] = n } }
    suspend fun setSonido(s: Boolean) { context.dataStore.edit { it[Keys.SONIDO] = s } }
    suspend fun setChatSecurity(s: Boolean) { context.dataStore.edit { it[Keys.CHAT_SECURITY] = s } }
    suspend fun setCalidad(c: Int) { context.dataStore.edit { it[Keys.CALIDAD] = c } }
}
