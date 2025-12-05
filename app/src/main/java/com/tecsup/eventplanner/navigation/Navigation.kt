package com.tecsup.eventplanner.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.tecsup.eventplanner.ui.screens.auth.LoginScreen
import com.tecsup.eventplanner.ui.screens.auth.RegisterScreen
import com.tecsup.eventplanner.ui.screens.events.CreateEventScreen
import com.tecsup.eventplanner.ui.screens.events.EditEventScreen
import com.tecsup.eventplanner.ui.screens.events.EventListScreen
import com.tecsup.eventplanner.ui.viewmodel.AuthViewModel

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object EventList : Screen("event_list")
    object CreateEvent : Screen("create_event")
    object EditEvent : Screen("edit_event/{eventId}") {
        fun createRoute(eventId: String) = "edit_event/$eventId"
    }
}

@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController(),
    authViewModel: AuthViewModel = viewModel()
) {
    val startDestination = if (authViewModel.isUserLoggedIn()) {
        Screen.EventList.route
    } else {
        Screen.Login.route
    }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // Login screen
        composable(Screen.Login.route) {
            LoginScreen(
                onNavigateToRegister = {
                    navController.navigate(Screen.Register.route)
                },
                onLoginSuccess = {
                    navController.navigate(Screen.EventList.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        // Register screen
        composable(Screen.Register.route) {
            RegisterScreen(
                onNavigateToLogin = {
                    navController.popBackStack()
                },
                onRegisterSuccess = {
                    navController.navigate(Screen.EventList.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        // Event list screen
        composable(Screen.EventList.route) {
            EventListScreen(
                onNavigateToCreateEvent = {
                    navController.navigate(Screen.CreateEvent.route)
                },
                onNavigateToEditEvent = { eventId ->
                    navController.navigate(Screen.EditEvent.createRoute(eventId))
                },
                onLogout = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        // Create event screen
        composable(Screen.CreateEvent.route) {
            CreateEventScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        // Edit event screen
        composable(
            route = Screen.EditEvent.route,
            arguments = listOf(
                navArgument("eventId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val eventId = backStackEntry.arguments?.getString("eventId") ?: return@composable
            EditEventScreen(
                eventId = eventId,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}