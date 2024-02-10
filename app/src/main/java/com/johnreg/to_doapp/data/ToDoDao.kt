package com.johnreg.to_doapp.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.johnreg.to_doapp.data.models.ToDoData

@Dao
interface ToDoDao {

    // LiveData - we will be able to observe data changes of this LiveData object from the Fragment
    @Query("SELECT * FROM todo_table ORDER BY id ASC")
    fun getAllData(): LiveData<List<ToDoData>>

    /*
    OnConflictStrategy - when a new item that we already have comes into our database,
    we can specify a strategy on what should our Room database do

    suspend - tell the compiler that our function will be running inside a coroutine
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertData(toDoData: ToDoData)

    @Update
    suspend fun updateData(toDoData: ToDoData)

    @Delete
    suspend fun deleteItem(toDoData: ToDoData)

    @Query("DELETE FROM todo_table")
    suspend fun deleteAll()

    // The LIKE operator is used in a WHERE clause to search for a specified pattern in a column
    @Query("SELECT * FROM todo_table WHERE title LIKE :searchQuery")
    fun searchDatabase(searchQuery: String): LiveData<List<ToDoData>>

}