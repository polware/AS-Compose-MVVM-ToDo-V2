package com.polware.todov2compose.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.polware.todov2compose.data.models.ToDoTask
import kotlinx.coroutines.flow.Flow

@Dao
interface ToDoDao {

    @Query("SELECT * FROM tasks_table ORDER BY id ASC")
    fun getAllTasks(): Flow<List<ToDoTask>>

    @Query("SELECT * FROM tasks_table WHERE id=:taskId")
    fun getSelectedTask(taskId: Int): Flow<ToDoTask>

    // Use Suspend for functions that run inside coroutine
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addTask(toDoTask: ToDoTask)

    @Update
    suspend fun updateTask(toDoTask: ToDoTask)

    @Delete
    suspend fun deleteTask(toDoTask: ToDoTask)

    @Query("DELETE FROM tasks_table")
    suspend fun deleteAllTasks()

    @Query("SELECT * FROM tasks_table WHERE title LIKE :searchQuery OR description LIKE :searchQuery")
    fun searchInDatabase(searchQuery: String): Flow<List<ToDoTask>>

    @Query("""
            SELECT * FROM tasks_table ORDER BY CASE
            WHEN priority LIKE 'L%' THEN 1
            WHEN priority LIKE 'M%' THEN 2
            WHEN priority LIKE 'H%' THEN 3
            END
        """)
    fun sortByLowPriority(): Flow<List<ToDoTask>>

    @Query("""
        SELECT * FROM tasks_table ORDER BY CASE 
        WHEN priority LIKE 'H%' THEN 1 
        WHEN priority LIKE 'M%' THEN 2 
        WHEN priority LIKE 'L%' THEN 3 
        END
    """)
    fun sortByHighPriority(): Flow<List<ToDoTask>>

}