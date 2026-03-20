package com.mak.notex

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import com.mak.notex.data.utils.NetworkMonitor
import com.mak.notex.presentation.auth.AuthState
import com.mak.notex.presentation.auth.AuthViewModel
import com.mak.notex.presentation.navigation.RootNavHost
import com.mak.notex.presentation.ui.theme.YTTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val authViewModel: AuthViewModel by viewModels()

    @Inject
    lateinit var networkMonitor: NetworkMonitor

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        splashScreen.setKeepOnScreenCondition { authViewModel.authState.value.shouldKeepSplashScreen() }
        setContent {
            YTTheme {
                val appState = rememberYTAppState(networkMonitor)
                val authState by authViewModel.authState.collectAsStateWithLifecycle()
                val navController = rememberNavController()

                RootNavHost(
                    navController = navController,
                    authState = authState,
                    appState = appState
                )
            }
        }
    }
}

@Composable
fun rememberYTAppState(
    networkMonitor: NetworkMonitor,
    coroutineScope: CoroutineScope = rememberCoroutineScope()
): YTAppState {
    return remember(networkMonitor, coroutineScope) {
        YTAppState(
            networkMonitor = networkMonitor,
            coroutineScope = coroutineScope
        )
    }
}

@Stable
class YTAppState(networkMonitor: NetworkMonitor, coroutineScope: CoroutineScope) {
    val isOffline: StateFlow<Boolean> =
        networkMonitor.isOnline
            .map(Boolean::not)
            .stateIn(
                scope = coroutineScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = false
            )
}