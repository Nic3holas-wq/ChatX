package com.example.chatx

import android.annotation.SuppressLint
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.fragment.app.FragmentActivity
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.chatx.view.Account
import com.example.chatx.view.Chat
import com.example.chatx.view.CreateGroup
import com.example.chatx.view.Home
import com.example.chatx.view.Settings
import com.example.chatx.view.Signin
import com.example.chatx.view.SplashScreen
import com.example.chatx.view.StartChatScreen
import com.example.chatx.view.Themes

@SuppressLint("ContextCastToActivity")
@Composable
fun AppNavigator(){
    val navController = rememberNavController()
    val context = LocalContext.current
    val activity = context as? FragmentActivity
    Log.d("AppNavigator", "Context type: ${context::class.java.simpleName}")
    NavHost(navController = navController, startDestination = "splash_screen"){
        composable("splash_screen"){
            if (activity != null){
                SplashScreen(
                    navController,
                    onAuthSuccess = {
                        //Navigate to home if authentication succeeds
                        navController.navigate("home"){
                            popUpTo("splash_screen"){inclusive = true}
                        }
                    },
                    onAuthFail = {
                        //You can show a toast or go to sign-in screen
                        navController.navigate("signin"){
                            popUpTo("splash_screen"){inclusive = true}
                        }
                    },
                    activity = activity
                )
            } else {
                Toast.makeText(context, "Activity not found!", Toast.LENGTH_LONG).show()
            }

        }
        composable("signin"){ Signin(navController) }
        composable("home"){ Home(navController) }
        composable("start_chat"){ StartChatScreen(navController) }
        composable("account_info"){ Account(navController) }
        composable("create_group"){ CreateGroup(navController) }
        composable("settings"){ Settings(navController) }
        composable("theme"){ Themes(navController) }
        composable("chat/{userId}"){backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId")
            Chat(navController ,userId = userId)
        }
    }
}