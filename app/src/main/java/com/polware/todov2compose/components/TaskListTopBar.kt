package com.polware.todov2compose.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.polware.todov2compose.R
import com.polware.todov2compose.data.models.Priority
import com.polware.todov2compose.ui.ToDoViewModel
import com.polware.todov2compose.ui.theme.MEDIUM_PADDING
import com.polware.todov2compose.ui.theme.topAppBarBackgroundColor
import com.polware.todov2compose.ui.theme.topAppBarContentColor
import com.polware.todov2compose.utils.Action
import com.polware.todov2compose.utils.SearchBarState

@Composable
fun TaskListTopBar(
    toDoViewModel: ToDoViewModel,
    searchBarState: SearchBarState,
    searchTextState: String
) {
    when(searchBarState) {
        SearchBarState.CLOSED -> {
            DefaultTaskListTopBar(
                onSearchClicked = {
                    toDoViewModel.updateSearchBarState(SearchBarState.OPENED)
                },
                onSortClicked = {
                    toDoViewModel.persistSortState(it)
                },
                onDeleteAllClick = {
                    toDoViewModel.updateAction(newAction = Action.DELETE_ALL)
                }
            )
        }
        else -> {
            SearchTopBar(
                searchText = searchTextState,
                onTextChange = {
                    toDoViewModel.updateSearchTextState(newSearchTextState = it)
                },
                onCloseClicked = {
                    toDoViewModel.updateSearchBarState(SearchBarState.CLOSED)
                    toDoViewModel.updateSearchTextState(newSearchTextState = "")
                },
                onSearchClicked = {
                    toDoViewModel.databaseSearch(searchQuery = it)
                }
            )
        }
    }

}

@Composable
fun DefaultTaskListTopBar(
    onSearchClicked: () -> Unit,
    onSortClicked: (Priority) -> Unit,
    onDeleteAllClick: () -> Unit
) {
    TopAppBar(
        title = {
            Text(text = stringResource(id = R.string.title_top_bar),
                color = MaterialTheme.colors.topAppBarContentColor)
        },
        actions = {
            TaskListTopBarActions(
                onSearchClicked = onSearchClicked,
                onSortClicked = onSortClicked,
                onDeleteAllClick = onDeleteAllClick
            )
        },
        backgroundColor = MaterialTheme.colors.topAppBarBackgroundColor
    )
}

@Composable
fun TaskListTopBarActions(
    onSearchClicked: () -> Unit,
    onSortClicked: (Priority) -> Unit,
    onDeleteAllClick: () -> Unit
) {
    var openDialog by remember {
        mutableStateOf(false)
    }
    DeleteAlertDialog(
        title = stringResource(id = R.string.delete_all_tasks),
        message = stringResource(id = R.string.delete_all_message_confirmation),
        openDialog = openDialog,
        closeDialog = {
            openDialog = false
            },
        onYesClick = {
            onDeleteAllClick()
        }
    )
    SearchAction(onSearchClicked = onSearchClicked)
    SortAction(onSortClicked = onSortClicked)
    DeleteAllAction(
        onDeleteAllClick = {
            openDialog = true
        }
    )
}

@Composable
fun SearchAction(onSearchClicked: () -> Unit) {
    IconButton(
        onClick = { onSearchClicked() }
    ) {
        Icon(
            imageVector = Icons.Filled.Search,
            contentDescription = stringResource(id = R.string.search_action),
            tint = MaterialTheme.colors.topAppBarContentColor
        )
    }
}

@Composable
fun SortAction(
    onSortClicked: (Priority) -> Unit
) {
    var expanded by remember {
        mutableStateOf(false)
    }

    IconButton(
        onClick = {
            expanded = true
        }
    ) {
        Icon(
            painter = painterResource(id = R.drawable.icon_filter_list),
            contentDescription = "Sort tasks",
            tint = MaterialTheme.colors.topAppBarContentColor
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = {
                expanded = false
            }
        ) {
            Priority.values().slice(setOf(0, 2, 3)).forEach {
                priorityName ->
                DropdownMenuItem(
                    onClick = {
                        expanded = false
                        onSortClicked(priorityName)
                    }
                ) {
                    PriorityItem(priority = priorityName)
                }
            }
        }
    }
}

@Composable
fun DeleteAllAction(
    onDeleteAllClick: () -> Unit
) {
    var expandedMenu by remember {
        mutableStateOf(false)
    }

    IconButton(
        onClick = {
            expandedMenu = true
        }
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_more_vert),
            contentDescription = "Delete All",
            tint = MaterialTheme.colors.topAppBarContentColor
        )
        DropdownMenu(
            expanded = expandedMenu,
            onDismissRequest = {
                expandedMenu = false
            }
        ) {
            DropdownMenuItem(
                onClick = {
                    expandedMenu = false
                    onDeleteAllClick()
                }
            ) {
                Text(
                    modifier = Modifier.padding(start = MEDIUM_PADDING),
                    text = stringResource(id = R.string.delete_all_action),
                    style = TextStyle(fontWeight = FontWeight.Normal)
                )
            }
        }
    }
}


@Composable
@Preview
private fun DefaultTaskListTopBarPreview() {
    DefaultTaskListTopBar(
        onSearchClicked = {},
        onSortClicked = {},
        onDeleteAllClick = {}
    )
}