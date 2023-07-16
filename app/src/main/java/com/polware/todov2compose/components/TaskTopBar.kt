package com.polware.todov2compose.components

import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Update
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import com.polware.todov2compose.R
import com.polware.todov2compose.data.models.Priority
import com.polware.todov2compose.data.models.ToDoTask
import com.polware.todov2compose.ui.theme.topAppBarBackgroundColor
import com.polware.todov2compose.ui.theme.topAppBarContentColor
import com.polware.todov2compose.utils.Action

@Composable
fun TaskTopBar(
    selectedTask: ToDoTask?,
    navigateToTaskListScreen: (Action) -> Unit
) {
    if (selectedTask == null) {
        NewTaskTopBar(navigateToTaskListScreen = navigateToTaskListScreen)
    }
    else {
        UpdateTaskTopBar(
            selectedTask = selectedTask,
            navigateToTaskListScreen = navigateToTaskListScreen
        )
    }
}

@Composable
fun NewTaskTopBar(
    navigateToTaskListScreen: (Action) -> Unit
) {
    TopAppBar(
        navigationIcon = {
            BackAction(onBackClick = navigateToTaskListScreen)
        },
        title = {
            Text(
                text = stringResource(id = R.string.title_new_task_bar),
                color = MaterialTheme.colors.topAppBarContentColor
            )
        },
        backgroundColor = MaterialTheme.colors.topAppBarBackgroundColor,
        actions = {
            AddAction(onAddClick = navigateToTaskListScreen)
        }
    )
}

@Composable
fun BackAction(
    onBackClick: (Action) -> Unit
) {
    IconButton(onClick = {
        onBackClick(Action.NO_ACTION)
    }
    ) {
        Icon(
            imageVector = Icons.Filled.ArrowBack,
            contentDescription = "Back Icon",
            tint = MaterialTheme.colors.topAppBarContentColor
        )
    }
}

@Composable
fun AddAction(
    onAddClick: (Action) -> Unit
) {
    IconButton(onClick = {
        onAddClick(Action.ADD)
    }
    ) {
        Icon(
            imageVector = Icons.Filled.Save,
            contentDescription = "Save Icon",
            tint = MaterialTheme.colors.topAppBarContentColor
        )
    }
}

@Composable
fun UpdateTaskTopBar(
    selectedTask: ToDoTask,
    navigateToTaskListScreen: (Action) -> Unit
) {
    TopAppBar(
        navigationIcon = {
            CloseAction(onCloseClick = navigateToTaskListScreen)
        },
        title = {
            Text(
                text = selectedTask.title,
                color = MaterialTheme.colors.topAppBarContentColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        backgroundColor = MaterialTheme.colors.topAppBarBackgroundColor,
        actions = {
            UpdateTopBarActions(
                selectedTask = selectedTask,
                navigateToTaskListScreen = navigateToTaskListScreen
            )
        }
    )
}

@Composable
fun UpdateTopBarActions(
    selectedTask: ToDoTask,
    navigateToTaskListScreen: (Action) -> Unit
) {
    var openDialog by remember {
        mutableStateOf(false)
    }
    DeleteAlertDialog(
        title = stringResource(id = R.string.delete_task, selectedTask.title),
        message = stringResource(id = R.string.delete_message_confirmation, selectedTask.title),
        openDialog = openDialog,
        closeDialog = {
            openDialog = false
        },
        onYesClick = {
            navigateToTaskListScreen(Action.DELETE)
        }
    )
    UpdateAction(onUpdateClick = navigateToTaskListScreen)
    DeleteAction(onDeleteClick = {
        openDialog = true
        }
    )
}

@Composable
fun CloseAction(
    onCloseClick: (Action) -> Unit
) {
    IconButton(onClick = {
        onCloseClick(Action.NO_ACTION)
    }
    ) {
        Icon(
            imageVector = Icons.Filled.Close,
            contentDescription = "Close Icon",
            tint = MaterialTheme.colors.topAppBarContentColor
        )
    }
}

@Composable
fun UpdateAction(
    onUpdateClick: (Action) -> Unit
) {
    IconButton(onClick = {
        onUpdateClick(Action.UPDATE)
    }
    ) {
        Icon(
            imageVector = Icons.Filled.Update,
            contentDescription = "Update Icon",
            tint = MaterialTheme.colors.topAppBarContentColor
        )
    }
}

@Composable
fun DeleteAction(
    onDeleteClick: () -> Unit
) {
    IconButton(onClick = {
        onDeleteClick()
    }
    ) {
        Icon(
            imageVector = Icons.Filled.Delete,
            contentDescription = "Delete Icon",
            tint = MaterialTheme.colors.topAppBarContentColor
        )
    }
}


@Composable
@Preview
fun NewTaskTopBarPreview() {
    NewTaskTopBar(navigateToTaskListScreen = {})
}

@Composable
@Preview
fun UpdateTaskTopBarPreview() {
    UpdateTaskTopBar(
        selectedTask = ToDoTask(
            id = 1,
            title = "Learning Java 11",
            description = "Random text...",
            priority = Priority.HIGH
        ),
        navigateToTaskListScreen = {}
    )
}