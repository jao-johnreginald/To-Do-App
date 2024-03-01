package com.johnreg.to_doapp.data.repository

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

    suspend fun createItem(toDoData: ToDoData) = toDoDao.createItem(toDoData)

    suspend fun updateItem(toDoData: ToDoData) = toDoDao.updateItem(toDoData)

    suspend fun deleteItem(toDoData: ToDoData) = toDoDao.deleteItem(toDoData)

    suspend fun deleteAllItems() = toDoDao.deleteAllItems()

}