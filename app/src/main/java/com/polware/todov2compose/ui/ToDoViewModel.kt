package com.polware.todov2compose.ui

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.polware.todov2compose.data.models.Priority
import com.polware.todov2compose.data.models.ToDoTask
import com.polware.todov2compose.data.repositories.DataStoreRepository
import com.polware.todov2compose.data.repositories.ToDoRepository
import com.polware.todov2compose.utils.Action
import com.polware.todov2compose.utils.Constants.MAX_TITLE_LENGTH
import com.polware.todov2compose.utils.RequestState
import com.polware.todov2compose.utils.SearchBarState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.lang.Exception
import javax.inject.Inject

@HiltViewModel
class ToDoViewModel @Inject constructor(
    private val repository: ToDoRepository,
    private val dataStoreRepository: DataStoreRepository
): ViewModel() {

    // Default state for SearchTopBar is: CLOSED
    var searchBarState by mutableStateOf(SearchBarState.CLOSED)
        private set // This variable will be able to modify only by ViewModel class
    var searchTextState by mutableStateOf("")
        private set

    private val _allTasks = MutableStateFlow<RequestState<List<ToDoTask>>>(RequestState.Idle)
    val allTasks: StateFlow<RequestState<List<ToDoTask>>> = _allTasks

    private val _selectedTask: MutableStateFlow<ToDoTask?> = MutableStateFlow(null)
    val selectedTask: StateFlow<ToDoTask?> = _selectedTask

    var id by mutableStateOf(0)
        private set
    var title by mutableStateOf("")
        private set
    var description by mutableStateOf("")
        private set
    var priority by mutableStateOf(Priority.LOW)
        private set
    // Default value for action
    var action by mutableStateOf(Action.NO_ACTION)
        private set

    private val _searchTasks = MutableStateFlow<RequestState<List<ToDoTask>>>(RequestState.Idle)
    val searchTasks: StateFlow<RequestState<List<ToDoTask>>> = _searchTasks

    private val _sortState = MutableStateFlow<RequestState<Priority>> (RequestState.Idle)
    val sortState: StateFlow<RequestState<Priority>> = _sortState

    val lowPrioritySort: StateFlow<List<ToDoTask>> = repository.sortByLowPriority.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        emptyList()
    )
    val highPrioritySort: StateFlow<List<ToDoTask>> = repository.sortByHighPriority.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        emptyList()
    )

    init {
        getAllTasks()
        readSortState()
    }

    private fun getAllTasks() {
        _allTasks.value = RequestState.Loading
        try {
            viewModelScope.launch {
                repository.getAllTasks.collect {
                    _allTasks.value = RequestState.Success(it)
                }
            }
        } catch (exception: Exception) {
            _allTasks.value = RequestState.Error(exception)
        }
    }

    fun getSelectedTask(taskId: Int) {
        viewModelScope.launch {
            repository.getSelectedTask(taskId).collect {
                task ->
                _selectedTask.value = task
            }
        }
    }

    fun updateTaskClicked(selectedTask: ToDoTask?) {
        if (selectedTask != null) {
            id = selectedTask.id
            title = selectedTask.title
            description = selectedTask.description
            priority = selectedTask.priority
        }
        else {
            id = 0
            title = ""
            description = ""
            priority = Priority.LOW
        }
    }

    fun updateTitleByLength(newTitle: String) {
        if (newTitle.length < MAX_TITLE_LENGTH) {
            title = newTitle
        }
    }

    fun validateTextFields(): Boolean {
        return title.isNotEmpty() && description.isNotEmpty()
    }

    fun handleDatabaseActions(action: Action) {
        Log.d("handleDatabaseActions", "Triggered")
        when(action) {
            Action.ADD -> {
                addTask()
            }
            Action.UPDATE -> {
                updateTask()
            }
            Action.DELETE -> {
                deleteTask()
            }
            Action.DELETE_ALL -> {
                deleteAllTasks()
            }
            Action.UNDO -> {
                addTask()
            }
            else -> {

            }
        }
    }

    private fun addTask() {
        viewModelScope.launch(Dispatchers.IO) {
            val newTask = ToDoTask(
                title = title,
                description = description,
                priority = priority
            )
            repository.addTask(toDoTask = newTask)
        }
        // Close SearchBar when we are adding a new task after searching tasks
        searchBarState = SearchBarState.CLOSED
    }

    private fun updateTask() {
        viewModelScope.launch(Dispatchers.IO) {
            val updateTask = ToDoTask(
                id = id,
                title = title,
                description = description,
                priority = priority
            )
            repository.updateTask(updateTask)
        }
    }

    private fun deleteTask() {
        viewModelScope.launch(Dispatchers.IO) {
            val deleteTask = ToDoTask(
                id = id,
                title = title,
                description = description,
                priority = priority
            )
            repository.deleteTask(deleteTask)
        }
    }

    private fun deleteAllTasks() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteAllTasks()
        }
    }

    fun databaseSearch(searchQuery: String) {
        _searchTasks.value = RequestState.Loading
        try {
            viewModelScope.launch {
                repository.tasksSearch(searchQuery = "%$searchQuery%").collect {
                    searchResult ->
                    _searchTasks.value = RequestState.Success(searchResult)
                }
            }
        } catch (exception: Exception) {
            _searchTasks.value = RequestState.Error(exception)
        }
        searchBarState = SearchBarState.TRIGGERED
    }

    // This function get the name from Priority and then save in DataStore Preference
    fun persistSortState(priority: Priority) {
        viewModelScope.launch(Dispatchers.IO) {
            dataStoreRepository.persistSortState(priority = priority)
        }
    }

    private fun readSortState() {
        _sortState.value = RequestState.Loading
        try {
            viewModelScope.launch {
                dataStoreRepository.readSortState
                    .map {
                        Priority.valueOf(it)
                    }
                    .collect {
                        _sortState.value = RequestState.Success(it)
                }
            }
        } catch (exception: Exception) {
            _sortState.value = RequestState.Error(exception)
        }
    }

    // Updates the action variable only with this ViewModel
    fun updateAction(newAction: Action) {
        action = newAction
    }

    fun updateDescription(newDescription: String) {
        description = newDescription
    }

    fun updatePriority(newPriority: Priority) {
        priority = newPriority
    }

    fun updateSearchBarState(newSearchAppBarState: SearchBarState) {
        searchBarState = newSearchAppBarState
    }

    fun updateSearchTextState(newSearchTextState: String) {
        searchTextState = newSearchTextState
    }

}