package com.mak.youtubex.presentation.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.mak.youtubex.presentation.auth.signin.SignInScreen
import com.mak.youtubex.presentation.auth.signup.SignUpScreen

fun NavGraphBuilder.authNavGraph(
    navController: NavHostController,
    onNavigateToMain: () -> Unit
) {
    navigation(
        startDestination = Screen.SignIn.route,
        route = NavGraphs.AUTH
    ) {
        composable(Screen.SignIn.route) {
            SignInScreen(
                onNavigateToHome = onNavigateToMain,
                onNavigateToSignUp = {
                    navController.navigate(Screen.SignUp.route)
                }
            )
        }

        composable(Screen.SignUp.route) {
            SignUpScreen(
                onNavigateToSignIn = { navController.popBackStack() },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
