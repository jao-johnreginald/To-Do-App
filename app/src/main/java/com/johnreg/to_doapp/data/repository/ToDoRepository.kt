package com.johnreg.to_doapp.data.repository

import androidx.annotation.WorkerThread
import com.johnreg.to_doapp.data.models.ToDoData
import com.johnreg.to_doapp.data.room.ToDoDao
import kotlinx.coroutines.flow.Flow

class ToDoRepository(private val toDoDao: ToDoDao) {

    val getAllItems: Flow<List<ToDoData>> = toDoDao.getAllItems()

    val sortByHighPriority: Flow<List<ToDoData>> = toDoDao.sortByHighPriority()
    val sortByLowPriority: Flow<List<ToDoData>> = toDoDao.sortByLowPriority()

    fun getSearchedItems(
        searchQuery: String
    ): Flow<List<ToDoData>> = toDoDao.getSearchedItems(searchQuery)

    // @WorkerThread allows the operations to be done in a single thread
    @WorkerThread
    suspend fun createItem(toDoData: ToDoData) = toDoDao.createItem(toDoData)

    @WorkerThread
    suspend fun updateItem(toDoData: ToDoData) = toDoDao.updateItem(toDoData)

    @WorkerThread
    suspend fun deleteItem(toDoData: ToDoData) = toDoDao.deleteItem(toDoData)

    @WorkerThread
    suspend fun deleteAllItems() = toDoDao.deleteAllItems()

}