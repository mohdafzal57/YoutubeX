package com.mak.notex.presentation.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.BottomAppBarDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.mak.notex.YTAppState
import com.mak.notex.R
import com.mak.notex.presentation.auth.AuthState
import com.mak.notex.presentation.main.common.TOP_LEVEL_DESTINATIONS
import com.mak.notex.presentation.main.common.YTNavigationBar
import com.mak.notex.presentation.main.common.YootubeTopAppBar

val LocalSnackbarHostState = staticCompositionLocalOf<SnackbarHostState> {
    error("SnackbarHostState not provided")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RootNavHost(
    navController: NavHostController = rememberNavController(),
    authState: AuthState,
    appState: YTAppState
) {
    val snackbarHostState = remember { SnackbarHostState() }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Check if we're in main graph
    val isMainGraph = currentRoute in TOP_LEVEL_DESTINATIONS

    val isOffline by appState.isOffline.collectAsStateWithLifecycle()

    val notConnectedMessage = stringResource(R.string.not_connected)
    LaunchedEffect(isOffline) {
        if (isOffline) {
            snackbarHostState.showSnackbar(
                message = notConnectedMessage,
                duration = SnackbarDuration.Indefinite,
            )
        }
    }

    /***
     * If the global LaunchedEffect triggers just as the BootstrapRoute is finishing its job,
     * the currentDest might still report as "bootstrap" for a few milliseconds.
     * Without the check, the global listener might call navigate(NavGraphs.AUTH) a second time,
     * potentially messing up the backstack or triggering double transitions.*/

    // Global logout listener: Navigates to AUTH if authState becomes Unauthenticated
    LaunchedEffect(authState) {
        if (authState is AuthState.Unauthenticated) {
            // Snapshot-based:
            // Accessing this property does not trigger recomposition.
            // This is ideal for event-based logic or one-time checks
            val currentDest = navController.currentDestination?.route
            val isInAuth = navController.currentDestination?.parent?.route == NavGraphs.AUTH

            if (currentDest != "bootstrap" && !isInAuth) {
                navController.navigate(NavGraphs.AUTH) {
                    popUpTo(0) { inclusive = true }
                }
            }
        }
    }

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(
        canScroll = { currentRoute == Screen.Home.route || currentRoute == Screen.SocialFeed.route }
    )
    LaunchedEffect(currentRoute) {
        if (currentRoute != Screen.Home.route) {
            scrollBehavior.state.heightOffset = 0f
            scrollBehavior.state.contentOffset = 0f
        }
    }

    CompositionLocalProvider(LocalSnackbarHostState provides snackbarHostState) {
        Surface(
            contentColor = MaterialTheme.colorScheme.onBackground,
            color = MaterialTheme.colorScheme.background
        ) {
            Scaffold(
                modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
                containerColor = Color.Transparent,
                contentColor = MaterialTheme.colorScheme.onBackground,
                contentWindowInsets = WindowInsets(0, 0, 0, 0),
                snackbarHost = {
                    SnackbarHost(
                        snackbarHostState,
                        modifier = Modifier.windowInsetsPadding(
                            WindowInsets.safeDrawing.exclude(
                                WindowInsets.ime,
                            ),
                        ),
                    )
                },
                topBar = {
                    AnimatedVisibility(
                        visible = isMainGraph,
                        enter = slideInVertically(initialOffsetY = { -it }),
                        exit = slideOutVertically(targetOffsetY = { -it })
                    ) {
                        YootubeTopAppBar(
                            scrollBehavior = scrollBehavior,
                            onNavigateToSearch = {
                                navController.navigate(Screen.Search.route)
                            }
                        )
                    }
                },
                bottomBar = {
                    AnimatedVisibility(
                        visible = isMainGraph,
                        enter = slideInVertically(initialOffsetY = { it }),
                        exit = slideOutVertically(targetOffsetY = { it })
                    ) {
                        YTNavigationBar(
                            currentRoute = currentRoute,
                            onNavigate = { route ->
                                navController.navigate(route) {
                                    // Pop up to the start destination to avoid building up a large stack
                                    popUpTo(Screen.Home.route) {
                                        saveState = true
                                    }
                                    // Avoid multiple copies of the same destination
                                    launchSingleTop = true
                                    // Restore state when reselecting a previously selected item
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            ) { paddingValues ->
                NavHost(
                    navController = navController,
                    startDestination = "bootstrap",
                    modifier = if (isMainGraph) Modifier.padding(paddingValues) else Modifier,
                    enterTransition = { fadeIn(tween(200)) },
                    exitTransition = { fadeOut(tween(200)) },
                    popEnterTransition = { fadeIn(tween(200)) },
                    popExitTransition = { fadeOut(tween(200)) }
                ) {

                    composable("bootstrap") {
                        BootstrapRoute(
                            authState = authState,
                            onAuthenticated = {
                                navController.navigate(NavGraphs.MAIN) {
                                    popUpTo("bootstrap") { inclusive = true }
                                }
                            },
                            onUnauthenticated = {
                                navController.navigate(NavGraphs.AUTH) {
                                    popUpTo("bootstrap") { inclusive = true }
                                }
                            }
                        )
                    }

                    authNavGraph(
                        navController = navController,
                        onNavigateToMain = {
                            navController.navigate(NavGraphs.MAIN) {
                                popUpTo(NavGraphs.AUTH) { inclusive = true }
                            }
                        }
                    )

                    mainNavGraph(
                        navController = navController,
                        onNavigateToAuth = {
                            navController.navigate(NavGraphs.AUTH) {
                                popUpTo(NavGraphs.MAIN) { inclusive = true }
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun BootstrapRoute(
    authState: AuthState,
    onAuthenticated: () -> Unit,
    onUnauthenticated: () -> Unit
) {
    LaunchedEffect(authState) {
        when(authState) {
            AuthState.Authenticated -> onAuthenticated()
            AuthState.Loading -> Unit
            AuthState.Unauthenticated -> onUnauthenticated()
        }
    }
}