package com.polware.todov2compose.ui.screens

import android.annotation.SuppressLint
import android.content.Context
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.polware.todov2compose.components.TaskContent
import com.polware.todov2compose.components.TaskTopBar
import com.polware.todov2compose.data.models.ToDoTask
import com.polware.todov2compose.ui.ToDoViewModel
import com.polware.todov2compose.utils.Action

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "UnusedMaterialScaffoldPaddingParameter")
@Composable
fun TaskDetailsScreen(
    selectedTask: ToDoTask?,
    toDoViewModel: ToDoViewModel,
    navigateToTaskListScreen: (Action) -> Unit
) {
    val context = LocalContext.current
    // Function that handle the event of Back press button (navigate to TaskListScreen with No Action)
    BackHandler {
        navigateToTaskListScreen(Action.NO_ACTION)
    }
    Scaffold(
        topBar = {
            TaskTopBar(
                selectedTask = selectedTask,
                navigateToTaskListScreen = {
                    action ->
                    if (action == Action.NO_ACTION) {
                        navigateToTaskListScreen(action)
                    }
                    else {
                        if (toDoViewModel.validateTextFields()) {
                            navigateToTaskListScreen(action)
                        }
                        else {
                            displayToast(context = context)
                        }
                    }
                }
            )
        }
    ) {
        TaskContent(
            title = toDoViewModel.title,
            onTitleChange = {
                toDoViewModel.updateTitleByLength(it)
            },
            description = toDoViewModel.description,
            onDescriptionChange = {
                toDoViewModel.updateDescription(newDescription = it)
            },
            priority = toDoViewModel.priority,
            onPrioritySelected = {
                toDoViewModel.updatePriority(it)
            }
        )
    }
}

fun displayToast(context: Context) {
    Toast.makeText(context, "Fields Empty", Toast.LENGTH_SHORT).show()
}

