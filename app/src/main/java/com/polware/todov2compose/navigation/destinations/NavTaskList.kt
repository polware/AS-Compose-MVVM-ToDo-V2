package com.polware.todov2compose.navigation.destinations

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import com.google.accompanist.navigation.animation.composable
import androidx.navigation.navArgument
import com.polware.todov2compose.ui.ToDoViewModel
import com.polware.todov2compose.ui.screens.TaskListScreen
import com.polware.todov2compose.utils.Action
import com.polware.todov2compose.utils.Constants.TASK_LIST_ARGUMENT
import com.polware.todov2compose.utils.Constants.TASK_LIST_SCREEN
import com.polware.todov2compose.utils.toAction

@OptIn(ExperimentalAnimationApi::class)
fun NavGraphBuilder.taskListComposable(
    navigateToTaskDetailsScreen: (taskId: Int) -> Unit,
    toDoViewModel: ToDoViewModel
) {

    composable(route = TASK_LIST_SCREEN,
        arguments = listOf(navArgument(TASK_LIST_ARGUMENT) {
            type = NavType.StringType
        })
    ) {
        navBackStackEntry ->
        // Capture every Action executed from screens
        val appAction = navBackStackEntry.arguments?.getString(TASK_LIST_ARGUMENT).toAction()
        // Define default value for user actions
        var myAction by rememberSaveable {
            mutableStateOf(Action.NO_ACTION)
        }

        LaunchedEffect(key1 = myAction) {
            if (appAction != myAction) {
                myAction = appAction
                toDoViewModel.updateAction(newAction = appAction)
            }
        }
        // Observe "action" variable and pass it to TaskListScreen
        val databaseAction = toDoViewModel.action
        TaskListScreen(
            databaseAction = databaseAction,
            navigateToTaskDetailsScreen = navigateToTaskDetailsScreen,
            toDoViewModel = toDoViewModel
        )
    }

}