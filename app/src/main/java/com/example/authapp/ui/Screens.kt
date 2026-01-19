package com.example.authapp.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun LoginScreen(onLoginClick: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = "Login Screen")
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onLoginClick) {
                Text("Sign in (Fake)")
            }
        }
    }
}

@Composable
fun HomeScreen(onDetailClick: () -> Unit, onLogoutClick: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = "Welcome Home!")
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onDetailClick) {
                Text("Go to Details")
            }
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = onLogoutClick) {
                Text("Log Out")
            }
        }
    }
}
@Composable
fun DetailScreen(id: Int, onBackClick: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = "Detail Screen for ID: $id")
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onBackClick) {
                Text("Go Back")
            }
        }
    }
}