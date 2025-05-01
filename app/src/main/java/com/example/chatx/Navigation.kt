package com.example.chatx

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.chatx.view.Chat
import com.example.chatx.view.Home
import com.example.chatx.view.Signin
import com.example.chatx.view.SplashScreen

@Composable
fun AppNavigator(){
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "splash_screen"){
        composable("splash_screen"){ SplashScreen(navController) }
        composable("signin"){ Signin(navController) }
        composable("home"){ Home(navController) }
        composable("chat/{userId}"){backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId")
            Chat(navController ,userId = userId)
        }
    }
}