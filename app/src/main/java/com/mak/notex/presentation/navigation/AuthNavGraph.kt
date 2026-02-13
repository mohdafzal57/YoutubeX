package com.mak.notex.presentation.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.mak.notex.presentation.auth.signin.SignInRoute
import com.mak.notex.presentation.auth.signup.SignUpScreen

fun NavGraphBuilder.authNavGraph(
    navController: NavHostController,
    onNavigateToMain: () -> Unit
) {
    navigation(
        startDestination = Screen.SignIn.route,
        route = NavGraphs.AUTH
    ) {
        composable(Screen.SignIn.route) {
            SignInRoute(
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
