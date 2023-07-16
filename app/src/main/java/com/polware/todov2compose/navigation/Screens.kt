package com.polware.todov2compose.navigation

import androidx.navigation.NavHostController
import com.polware.todov2compose.utils.Action
import com.polware.todov2compose.utils.Constants.SPLASH_SCREEN
import com.polware.todov2compose.utils.Constants.TASK_LIST_SCREEN

class Screens(navHostController: NavHostController) {

    val splash: () -> Unit = {
        navHostController.navigate(route = "main/${Action.NO_ACTION.name}") {
            popUpTo(SPLASH_SCREEN) {
                inclusive = true // Inclusive: remove SplashScreen from the Back Stack
            }
        }
    }

    val taskList: (Int) -> Unit = {
        taskId ->
        navHostController.navigate(route = "task/$taskId")
    }

    val taskDetails: (Action) -> Unit = {
        action ->
        navHostController.navigate(route = "main/${action.name}") {
            popUpTo(TASK_LIST_SCREEN) { inclusive = true }
        }
    }

}