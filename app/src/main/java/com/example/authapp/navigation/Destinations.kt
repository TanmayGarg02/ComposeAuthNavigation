package com.example.authapp.navigation

import kotlinx.serialization.Serializable

sealed interface Screen

@Serializable
data object LoginScreen : Screen

@Serializable
data object HomeScreen: Screen

@Serializable
data class DetailScreen(val id: Int) : Screen