package com.johnreg.to_doapp.data.repository

import androidx.lifecycle.LiveData
import com.johnreg.to_doapp.data.ToDoDao
import com.johnreg.to_doapp.data.models.ToDoData

class ToDoRepository(private val toDoDao: ToDoDao) {

    val getAllData: LiveData<List<ToDoData>> = toDoDao.getAllData()

    suspend fun insertData(toDoData: ToDoData) {
        toDoDao.insertData(toDoData)
    }

}