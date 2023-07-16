package com.polware.todov2compose.navigation

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.polware.todov2compose.navigation.destinations.splashComposable
import com.polware.todov2compose.navigation.destinations.taskDetailsComposable
import com.polware.todov2compose.navigation.destinations.taskListComposable
import com.polware.todov2compose.ui.ToDoViewModel
import com.polware.todov2compose.utils.Constants.SPLASH_SCREEN

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AppNavigation(
    navHostController: NavHostController,
    toDoViewModel: ToDoViewModel) {

    val screen = remember(navHostController) {
        Screens(navHostController =  navHostController)
    }

    AnimatedNavHost(navController = navHostController, startDestination = SPLASH_SCREEN) {

        splashComposable(
            navigateToTaskListScreen = screen.splash
        )

        taskListComposable(
            navigateToTaskDetailsScreen = screen.taskList,
            toDoViewModel = toDoViewModel
        )

        taskDetailsComposable(
            toDoViewModel = toDoViewModel,
            navigateToTaskListScreen = screen.taskDetails
        )
    }

}