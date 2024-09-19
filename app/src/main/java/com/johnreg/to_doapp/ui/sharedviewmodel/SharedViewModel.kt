package com.johnreg.to_doapp.ui.sharedviewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.johnreg.to_doapp.data.models.ToDoData

class SharedViewModel(application: Application) : AndroidViewModel(application) {

    // Creates a MutableLiveData initialized with the given value
    private val _isDatabaseEmpty: MutableLiveData<Boolean> = MutableLiveData(false)
    val isDatabaseEmpty: LiveData<Boolean> get() = _isDatabaseEmpty

    fun setDatabaseEmpty(newDataList: List<ToDoData>) {
        _isDatabaseEmpty.value = newDataList.isEmpty()
    }

}