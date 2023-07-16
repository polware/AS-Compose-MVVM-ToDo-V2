package com.polware.todov2compose.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.DismissDirection
import androidx.compose.material.DismissValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FractionalThreshold
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.SwipeToDismiss
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.rememberDismissState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import com.polware.todov2compose.data.models.Priority
import com.polware.todov2compose.data.models.ToDoTask
import com.polware.todov2compose.ui.theme.HighPriority
import com.polware.todov2compose.ui.theme.LARGEST_PADDING
import com.polware.todov2compose.ui.theme.LARGE_PADDING
import com.polware.todov2compose.ui.theme.PRIORITY_INDICATOR_SIZE
import com.polware.todov2compose.ui.theme.TASK_ITEM_ELEVATION
import com.polware.todov2compose.ui.theme.taskItemBackgroundColor
import com.polware.todov2compose.ui.theme.taskItemTextColor
import com.polware.todov2compose.utils.Action
import com.polware.todov2compose.utils.RequestState
import com.polware.todov2compose.utils.SearchBarState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun TaskListContent(
    taskList: RequestState<List<ToDoTask>>,
    searchBarState: SearchBarState,
    searchTasks: RequestState<List<ToDoTask>>,
    sortState: RequestState<Priority>,
    lowPrioritySort: List<ToDoTask>,
    highPrioritySort: List<ToDoTask>,
    onSwipeToDelete: (Action, ToDoTask) -> Unit,
    navigateToTaskDetailsScreen: (taskId: Int) -> Unit
) {
    if (sortState is RequestState.Success) {
        when {
            searchBarState == SearchBarState.TRIGGERED -> {
                if (searchTasks is RequestState.Success) {
                    HandleContent(
                        tasks = searchTasks.data,
                        onSwipeToDelete = onSwipeToDelete,
                        navigateToTaskDetailsScreen = navigateToTaskDetailsScreen
                    )
                }
            }
            sortState.data == Priority.NONE -> {
                if (taskList is RequestState.Success) {
                    HandleContent(
                        tasks = taskList.data,
                        onSwipeToDelete = onSwipeToDelete,
                        navigateToTaskDetailsScreen = navigateToTaskDetailsScreen
                    )
                }
            }
            sortState.data == Priority.LOW -> {
                HandleContent(
                    tasks = lowPrioritySort,
                    onSwipeToDelete = onSwipeToDelete,
                    navigateToTaskDetailsScreen = navigateToTaskDetailsScreen
                )
            }
            sortState.data == Priority.HIGH -> {
                HandleContent(
                    tasks = highPrioritySort,
                    onSwipeToDelete = onSwipeToDelete,
                    navigateToTaskDetailsScreen = navigateToTaskDetailsScreen
                )
            }
        }
    }
}

@Composable
fun HandleContent(
    tasks: List<ToDoTask>,
    onSwipeToDelete: (Action, ToDoTask) -> Unit,
    navigateToTaskDetailsScreen: (taskId: Int) -> Unit
) {
    if (tasks.isEmpty()) {
        EmptyContent()
    }
    else {
        DisplayTasks(
            taskList = tasks,
            onSwipeToDelete = onSwipeToDelete,
            navigateToTaskDetailsScreen = navigateToTaskDetailsScreen
        )
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun DisplayTasks(
    taskList: List<ToDoTask>,
    onSwipeToDelete: (Action, ToDoTask) -> Unit,
    navigateToTaskDetailsScreen: (taskId: Int) -> Unit
) {
    LazyColumn {
        items(
            items = taskList,
            key = {
                    task ->
                task.id
            }
        ) {
            task ->
            val dismissState = rememberDismissState()
            val dismissDirection = dismissState.dismissDirection
            val isDismissed = dismissState.isDismissed(DismissDirection.EndToStart)
            val scope = rememberCoroutineScope()
            val degrees by animateFloatAsState(
                targetValue = if (dismissState.targetValue == DismissValue.Default) 0f else -45f
                    )
            var itemAppear by remember {
                mutableStateOf(false)
            }
            if (isDismissed && dismissDirection == DismissDirection.EndToStart) {
                LaunchedEffect(key1 = true) {
                    scope.launch {
                        delay(300)
                        onSwipeToDelete(Action.DELETE, task)
                    }
                }
            }
            LaunchedEffect(key1 = true) {
                itemAppear = true
            }
            // Apply animation to expand vertically
            AnimatedVisibility(
                visible = itemAppear && !isDismissed,
                enter = expandVertically(
                    animationSpec = tween(durationMillis = 300)
                ),
                exit = shrinkVertically(
                    animationSpec = tween(durationMillis = 300)
                )
            ) {
                SwipeToDismiss(
                    state = dismissState,
                    directions = setOf(DismissDirection.EndToStart), // Swipe from right to left side
                    dismissThresholds = {
                        FractionalThreshold(0.3f) // Dismiss/Delete task when swipe 30% of screen
                    },
                    background = {
                        RedBackground(degrees = degrees)
                    },
                    dismissContent = {
                        TaskItem(
                            toDoTask = task,
                            navigateToTaskDetailsScreen = navigateToTaskDetailsScreen
                        )
                    }
                )
            }
        }
    }
}

@Composable
fun RedBackground(degrees: Float) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(HighPriority)
            .padding(horizontal = LARGEST_PADDING),
        contentAlignment = Alignment.CenterEnd
    ) {
        Icon(
            modifier = Modifier.rotate(degrees = degrees),
            imageVector = Icons.Filled.Delete,
            contentDescription = "Delete Icon",
            tint = Color.White
        )
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun TaskItem(
    toDoTask: ToDoTask,
    navigateToTaskDetailsScreen: (taskId: Int) -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colors.taskItemBackgroundColor,
        shape = RectangleShape,
        elevation = TASK_ITEM_ELEVATION,
        onClick = {
            navigateToTaskDetailsScreen(toDoTask.id)
        }
    ) {
        Column(
            modifier = Modifier
                .padding(all = LARGE_PADDING)
                .fillMaxSize()
        ) {
            Row {
                Text(
                    modifier = Modifier.weight(8f),
                    text = toDoTask.title,
                    color = MaterialTheme.colors.taskItemTextColor,
                    style = MaterialTheme.typography.h5,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.TopEnd
                ) {
                    Canvas(
                        modifier = Modifier
                            .size(PRIORITY_INDICATOR_SIZE)
                    ) {
                        drawCircle(
                            color = toDoTask.priority.color
                        )
                    }
                }
            }
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = toDoTask.description,
                color = MaterialTheme.colors.taskItemTextColor,
                style = MaterialTheme.typography.subtitle1,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}


@Composable
@Preview
fun TaskItemPreview() {
    TaskItem(
        toDoTask = ToDoTask(0, "Title", "Random text", Priority.LOW),
        navigateToTaskDetailsScreen = {}
    )
}