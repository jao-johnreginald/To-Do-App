package com.johnreg.to_doapp.data.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.johnreg.to_doapp.data.models.ToDoData
import kotlinx.coroutines.flow.Flow

@Dao
interface ToDoDao {

    /*
    Convert Flow from coroutines into LiveData in ViewModel
    No need to use suspend because Room already does this without using the main thread
     */
    @Query("SELECT * FROM todo_table ORDER BY id ASC")
    fun getAllItems(): Flow<List<ToDoData>>

    /*
    OnConflictStrategy - when a new item that we already have comes into our database,
    we can specify a strategy on what should our Room database do

    suspend - tell the compiler that our function will be running inside a coroutine
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun createItem(toDoData: ToDoData)

    @Update
    suspend fun updateItem(toDoData: ToDoData)

    @Delete
    suspend fun deleteItem(toDoData: ToDoData)

    @Query("DELETE FROM todo_table")
    suspend fun deleteAllItems()

    // The LIKE operator is used in a WHERE clause to search for a specified pattern in a column
    @Query("SELECT * FROM todo_table WHERE title LIKE :searchQuery")
    fun getSearchedItems(searchQuery: String): Flow<List<ToDoData>>

    /*
    H% - search for the priorities that start with H, meaning HIGH
    1 - return the high priority results first
     */
    @Query("SELECT * FROM todo_table ORDER BY CASE" +
            " WHEN priority LIKE 'H%' THEN 1" +
            " WHEN priority LIKE 'M%' THEN 2" +
            " WHEN priority LIKE 'L%' THEN 3" +
            " END")
    fun sortByHighPriority(): Flow<List<ToDoData>>

    @Query("SELECT * FROM todo_table ORDER BY CASE" +
            " WHEN priority LIKE 'L%' THEN 1" +
            " WHEN priority LIKE 'M%' THEN 2" +
            " WHEN priority LIKE 'H%' THEN 3" +
            " END")
    fun sortByLowPriority(): Flow<List<ToDoData>>

}