package com.polware.todov2compose.navigation.destinations

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import com.google.accompanist.navigation.animation.composable
import androidx.navigation.navArgument
import com.polware.todov2compose.ui.ToDoViewModel
import com.polware.todov2compose.ui.screens.TaskDetailsScreen
import com.polware.todov2compose.utils.Action
import com.polware.todov2compose.utils.Constants
import com.polware.todov2compose.utils.Constants.TASK_DETAILS_ARGUMENT

@OptIn(ExperimentalAnimationApi::class)
fun NavGraphBuilder.taskDetailsComposable(
    toDoViewModel: ToDoViewModel,
    navigateToTaskListScreen: (Action) -> Unit
) {
    composable(route = Constants.TASK_DETAILS_SCREEN,
        arguments = listOf(navArgument(Constants.TASK_DETAILS_ARGUMENT) {
            type = NavType.IntType
        }),
        enterTransition = {
            slideInHorizontally(
                initialOffsetX = { fullWidth -> -fullWidth },
                animationSpec = tween(400) // Animate screen from left to right side
            )
        }
    ) {
        navBackStackEntry ->
        val taskId = navBackStackEntry.arguments!!.getInt(TASK_DETAILS_ARGUMENT)
        LaunchedEffect(key1 = taskId) {
            toDoViewModel.getSelectedTask(taskId= taskId)
        }

        val selectedTask by toDoViewModel.selectedTask.collectAsState()
        LaunchedEffect(key1 = selectedTask) {
            // Updates screen only on update or add task
            if (selectedTask != null || taskId == -1) {
                toDoViewModel.updateTaskClicked(selectedTask = selectedTask)
            }
        }
        TaskDetailsScreen(
            selectedTask = selectedTask,
            toDoViewModel = toDoViewModel,
            navigateToTaskListScreen = navigateToTaskListScreen
        )
    }

}