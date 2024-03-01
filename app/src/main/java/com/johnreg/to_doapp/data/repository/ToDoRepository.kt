package com.johnreg.to_doapp.data.repository

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import com.johnreg.to_doapp.data.room.ToDoDao
import com.johnreg.to_doapp.data.models.ToDoData

class ToDoRepository(private val toDoDao: ToDoDao) {

    val getAllItems: LiveData<List<ToDoData>> = toDoDao.getAllItems()

    val sortByHighPriority: LiveData<List<ToDoData>> = toDoDao.sortByHighPriority()
    val sortByLowPriority: LiveData<List<ToDoData>> = toDoDao.sortByLowPriority()

    fun getSearchedItems(searchQuery: String): LiveData<List<ToDoData>> {
        return toDoDao.getSearchedItems(searchQuery)
    }

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