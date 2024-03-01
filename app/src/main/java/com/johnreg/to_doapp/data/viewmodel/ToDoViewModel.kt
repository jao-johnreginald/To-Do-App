package com.johnreg.to_doapp.data.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
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

    // LiveData - we will be able to observe data changes of this LiveData object from the Fragment
    val getAllItems: LiveData<List<ToDoData>> = repository.getAllItems.asLiveData()

    val sortByHighPriority: LiveData<List<ToDoData>> = repository.sortByHighPriority.asLiveData()
    val sortByLowPriority: LiveData<List<ToDoData>> = repository.sortByLowPriority.asLiveData()

    fun getSearchedItems(searchQuery: String): LiveData<List<ToDoData>> {
        return repository.getSearchedItems(searchQuery).asLiveData()
    }

    // Run function from a background thread, Dispatchers.IO is generally used for database operations
    fun createItem(toDoData: ToDoData) = viewModelScope.launch(Dispatchers.IO) {
        repository.createItem(toDoData)
    }

    fun updateItem(toDoData: ToDoData) = viewModelScope.launch(Dispatchers.IO) {
        repository.updateItem(toDoData)
    }

    fun deleteItem(toDoData: ToDoData) = viewModelScope.launch(Dispatchers.IO) {
        repository.deleteItem(toDoData)
    }

    fun deleteAllItems() = viewModelScope.launch(Dispatchers.IO) {
        repository.deleteAllItems()
    }

}