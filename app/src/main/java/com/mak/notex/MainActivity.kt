package com.mak.notex

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.getValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import com.mak.notex.presentation.auth.AuthViewModel
import com.mak.notex.presentation.navigation.NavGraphs
import com.mak.notex.presentation.navigation.RootNavGraph
import com.mak.notex.presentation.ui.theme.NoteXTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        // Keep the splash screen on-screen until we know the auth state
        splashScreen.setKeepOnScreenCondition {
            authViewModel.isLoggedIn.value == null
        }

        enableEdgeToEdge()

        setContent {
            NoteXTheme {
                val connectivityViewModel = hiltViewModel<ConnectivityViewModel>()
                val isLoggedIn by authViewModel.isLoggedIn.collectAsStateWithLifecycle()
                val isOffline by connectivityViewModel.isOffline.collectAsStateWithLifecycle()

                // Only render the navigation once we have a definitive auth state
                if (isLoggedIn != null) {
                    val navController = rememberNavController()
                    val startDest = if (isLoggedIn == true) NavGraphs.MAIN else NavGraphs.AUTH

                    RootNavGraph(
                        navController = navController,
                        startDestination = startDest,
                        isOffline = isOffline
                    )
                }
            }
        }
    }
}
