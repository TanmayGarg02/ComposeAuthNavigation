package com.example.authapp.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.authapp.auth.AuthViewModel
import com.example.authapp.auth.GoogleAuthClient
import com.example.authapp.ui.DetailScreen
import com.example.authapp.ui.HomeScreen
import com.example.authapp.ui.LoginScreen

import kotlinx.coroutines.launch

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val applicationScope = rememberCoroutineScope()

    val viewModel = viewModel<AuthViewModel>()
    val googleAuthClient = GoogleAuthClient(context)

    NavHost(navController = navController, startDestination = LoginScreen) {

        composable<LoginScreen> {
            val state by viewModel.state.collectAsState()

            LaunchedEffect(key1 = Unit) {
                if (googleAuthClient.getSignedInUser() != null) {
                    navController.navigate(HomeScreen)
                }
            }

            LaunchedEffect(key1 = state.isSignInSuccessful) {
                if (state.isSignInSuccessful) {
                    navController.navigate(HomeScreen)
                    viewModel.resetState()
                }
            }

            LoginScreen(
                state = state,
                onSignInClick = {
                    applicationScope.launch {
                        val result = googleAuthClient.signIn()
                        viewModel.onSignInResult(result)
                    }
                }
            )
        }

        composable<HomeScreen> {
            HomeScreen(
                onDetailClick = {
                    navController.navigate(DetailScreen(id = 123))
                },
                onLogoutClick = {
                    applicationScope.launch {
                        googleAuthClient.signOut()
                        navController.navigate(LoginScreen)
                        viewModel.resetState()
                    }
                }
            )
        }

        composable<DetailScreen> { backStackEntry ->
            val args = backStackEntry.toRoute<DetailScreen>()
            DetailScreen(
                id = args.id,
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}