package com.example.authapp.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.authapp.ui.DetailScreen
import com.example.authapp.ui.HomeScreen
import com.example.authapp.ui.LoginScreen


@Composable
fun AppNavigation(){
    val controller = rememberNavController()
    NavHost(navController = controller, startDestination = LoginScreen){

        composable<LoginScreen> {
            LoginScreen(
                onLoginClick = {
                    controller.navigate(HomeScreen) {
                        popUpTo(LoginScreen) { inclusive = true }
                    }
                }
            )
        }

        composable<HomeScreen> {
            HomeScreen(
                onDetailClick = {
                    controller.navigate(DetailScreen(id = 99))
                },
                onLogoutClick = {
                    controller.navigate(LoginScreen) {
                        popUpTo(0)
                    }
                }
            )
        }

        composable<DetailScreen> { backStackEntry ->
            val route: DetailScreen = backStackEntry.toRoute()

            DetailScreen(
                id = route.id,
                onBackClick = { controller.popBackStack() }
            )
        }
        
    }
}