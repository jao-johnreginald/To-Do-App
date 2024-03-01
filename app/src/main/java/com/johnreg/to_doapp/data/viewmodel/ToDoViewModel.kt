package com.johnreg.to_doapp.data.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.johnreg.to_doapp.data.room.ToDoDatabase
import com.johnreg.to_doapp.data.models.ToDoData
import com.johnreg.to_doapp.data.repository.ToDoRepository
import com.johnreg.to_doapp.data.room.ToDoDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ToDoViewModel(application: Application) : AndroidViewModel(application) {

    private val database: ToDoDatabase = ToDoDatabase.getDatabase(application)
    private val toDoDao: ToDoDao = database.getToDoDao()
    private val repository: ToDoRepository = ToDoRepository(toDoDao)

    val getAllItems: LiveData<List<ToDoData>> = repository.getAllItems

    val sortByHighPriority: LiveData<List<ToDoData>> = repository.sortByHighPriority
    val sortByLowPriority: LiveData<List<ToDoData>> = repository.sortByLowPriority

    fun getSearchedItems(searchQuery: String): LiveData<List<ToDoData>> {
        return repository.getSearchedItems(searchQuery)
    }

    fun createItem(toDoData: ToDoData) {
        // run insertData() from a background thread
        viewModelScope.launch(Dispatchers.IO) {
            repository.createItem(toDoData)
        }
    }

    fun updateItem(toDoData: ToDoData) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateItem(toDoData)
        }
    }

    fun deleteItem(toDoData: ToDoData) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteItem(toDoData)
        }
    }

    fun deleteAllItems() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteAllItems()
        }
    }

}