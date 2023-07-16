package com.polware.todov2compose.ui.screens

import android.annotation.SuppressLint
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.SnackbarResult
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.polware.todov2compose.R
import com.polware.todov2compose.components.TaskListContent
import com.polware.todov2compose.components.TaskListTopBar
import com.polware.todov2compose.ui.ToDoViewModel
import com.polware.todov2compose.ui.theme.fabBackgroundColor
import com.polware.todov2compose.utils.Action
import com.polware.todov2compose.utils.SearchBarState
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun TaskListScreen(
    databaseAction: Action,
    navigateToTaskDetailsScreen: (taskId: Int) -> Unit,
    toDoViewModel: ToDoViewModel
) {
    LaunchedEffect(key1 = databaseAction) {
        toDoViewModel.handleDatabaseActions(action = databaseAction)
    }

    val allTasks by toDoViewModel.allTasks.collectAsState()
    val searchBarState: SearchBarState = toDoViewModel.searchBarState
    val searchTextSate: String = toDoViewModel.searchTextState
    val scaffoldState = rememberScaffoldState()
    val searchTasks by toDoViewModel.searchTasks.collectAsState()
    val sortState by toDoViewModel.sortState.collectAsState()
    val lowPrioritySort by toDoViewModel.lowPrioritySort.collectAsState()
    val highPrioritySort by toDoViewModel.highPrioritySort.collectAsState()

    DisplaySnackBar(
        scaffoldState = scaffoldState,
        onComplete = {
            toDoViewModel.updateAction(newAction = it)
        },
        taskTitle = toDoViewModel.title,
        action = databaseAction,
        onUndoClick = {
            toDoViewModel.updateAction(newAction = it)
        }
    )

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            TaskListTopBar(
                toDoViewModel = toDoViewModel,
                searchBarState = searchBarState,
                searchTextState = searchTextSate
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                navigateToTaskDetailsScreen(-1)
                },
                backgroundColor = MaterialTheme.colors.fabBackgroundColor
            ) {
                Icon(imageVector = Icons.Filled.Add,
                    contentDescription = stringResource(id = R.string.fab_button),
                    tint = Color.White
                )
            }
        }
    ) {
        TaskListContent(
            taskList = allTasks,
            searchBarState = searchBarState,
            searchTasks = searchTasks,
            sortState = sortState,
            lowPrioritySort = lowPrioritySort,
            highPrioritySort = highPrioritySort,
            onSwipeToDelete = {
                action, task ->
                toDoViewModel.updateAction(newAction = action)
                toDoViewModel.updateTaskClicked(selectedTask = task)
                scaffoldState.snackbarHostState.currentSnackbarData?.dismiss()
            },
            navigateToTaskDetailsScreen = navigateToTaskDetailsScreen
        )
    }
}

@Composable
fun DisplaySnackBar(
    scaffoldState: ScaffoldState,
    onComplete: (Action) -> Unit,
    taskTitle: String,
    action: Action,
    onUndoClick: (Action) -> Unit
) {
    val scope = rememberCoroutineScope()
    LaunchedEffect(key1 = action) {
        if (action != Action.NO_ACTION) {
            scope.launch {
                val snackBarResult = scaffoldState.snackbarHostState.showSnackbar(
                    message = setMessage(action = action, taskTitle= taskTitle),
                    actionLabel = setActionLabel(action = action)
                )
                undoDeletedTask(
                    action = action,
                    snackBarResult = snackBarResult,
                    onUndoClick = onUndoClick
                )
            }
            onComplete(Action.NO_ACTION)
        }
    }
}

private fun setMessage(
    action: Action,
    taskTitle: String
): String {
    return when(action) {
        Action.DELETE_ALL -> "All tasks removed!"
        else -> "$taskTitle was been: ${action.name}"
    }
}

private fun setActionLabel(action: Action): String {
    return if (action.name == "DELETE") {
        "UNDO"
    } else {
        "Ok"
    }
}

private fun undoDeletedTask(
    action: Action,
    snackBarResult: SnackbarResult,
    onUndoClick: (Action) -> Unit
) {
    if (snackBarResult == SnackbarResult.ActionPerformed && action == Action.DELETE) {
        onUndoClick(Action.UNDO)
    }
}