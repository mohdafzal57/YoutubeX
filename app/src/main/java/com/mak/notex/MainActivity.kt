package com.mak.notex

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import com.mak.notex.presentation.auth.AuthState
import com.mak.notex.presentation.auth.AuthViewModel
import com.mak.notex.presentation.navigation.RootNavHost
import com.mak.notex.presentation.ui.theme.NoteXTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val authViewModel: AuthViewModel by viewModels()
    private val connectivityViewModel: ConnectivityViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            NoteXTheme {
                val authState by authViewModel.authState
                    .collectAsStateWithLifecycle()

                val isOffline by connectivityViewModel.isOffline
                    .collectAsStateWithLifecycle()

                LaunchedEffect(authState) {
                    splashScreen.setKeepOnScreenCondition {
                        authState is AuthState.Loading
                    }
                }

                val navController = rememberNavController()

                RootNavHost(
                    navController = navController,
                    authState = authState,
                    isOffline = isOffline
                )
            }
        }
    }
}






//                val view = LocalView.current
//                if (!view.isInEditMode) {
//                    SideEffect {
//                        val window = (view.context as Activity).window
//                        WindowCompat.setDecorFitsSystemWindows(window, false)
//                        val insetsController = WindowCompat.getInsetsController(window, view)
//                        insetsController.isAppearanceLightStatusBars = false
//                        insetsController.isAppearanceLightNavigationBars = false
//                    }
//                }