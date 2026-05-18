package com.farhan.loginapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.farhan.loginapp.data.AppDatabase
import com.farhan.loginapp.data.UserRepository
import com.farhan.loginapp.ui.HomeScreen
import com.farhan.loginapp.ui.LoginScreen
import com.farhan.loginapp.ui.LoginViewModel
import com.farhan.loginapp.ui.theme.LoginAppTheme
import java.net.URLDecoder
import java.net.URLEncoder

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val database = AppDatabase.getDatabase(this)
        val repository = UserRepository(database.userDao())

        setContent {
            LoginAppTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    val navController = rememberNavController()
                    val loginViewModel: LoginViewModel = viewModel()

                    NavHost(navController = navController, startDestination = "login") {
                        composable("login") {
                            LoginScreen(
                                viewModel = loginViewModel,
                                repository = repository,
                                onLoginSuccess = { username ->
                                    val encoded = URLEncoder.encode(username, "UTF-8")
                                    navController.navigate("home/$encoded") {
                                        popUpTo("login") { inclusive = true }
                                    }
                                }
                            )
                        }
                        composable(
                            route = "home/{username}",
                            arguments = listOf(navArgument("username") { type = NavType.StringType })
                        ) { backStackEntry ->
                            val encodedUsername = backStackEntry.arguments?.getString("username") ?: ""
                            val username = URLDecoder.decode(encodedUsername, "UTF-8")
                            HomeScreen(
                                username = username,
                                onLogout = {
                                    navController.navigate("login") {
                                        popUpTo("home/{username}") { inclusive = true }
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
