package cu.alexgi.youchat.core.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

enum class ConnectionState {
    NO_CONNECTION, CONNECTING, UPDATING, CONNECTED
}

@Singleton
class ConnectivityObserver @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val _connectionState = MutableStateFlow(ConnectionState.NO_CONNECTION)
    val connectionState: StateFlow<ConnectionState> = _connectionState
    
    private val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    
    fun observe() {
        connectivityManager.registerDefaultNetworkCallback(object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                _connectionState.value = ConnectionState.CONNECTED
            }
            override fun onLost(network: Network) {
                _connectionState.value = ConnectionState.NO_CONNECTION
            }
        })
    }
}
