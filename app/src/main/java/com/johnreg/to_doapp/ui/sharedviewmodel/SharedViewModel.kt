package com.johnreg.to_doapp.ui.sharedviewmodel

import android.app.Application
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.johnreg.to_doapp.R
import com.johnreg.to_doapp.data.models.Priority
import com.johnreg.to_doapp.data.models.ToDoData

class SharedViewModel(application: Application): AndroidViewModel(application) {

    val spinnerListener: OnItemSelectedListener = object : OnItemSelectedListener {
        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            when (position) {
                0 -> (parent?.getChildAt(0) as TextView).setTextColor(ContextCompat.getColor(application, R.color.red))
                1 -> (parent?.getChildAt(0) as TextView).setTextColor(ContextCompat.getColor(application, R.color.yellow))
                2 -> (parent?.getChildAt(0) as TextView).setTextColor(ContextCompat.getColor(application, R.color.green))
            }
        }
        override fun onNothingSelected(parent: AdapterView<*>?) {}
    }

    // Creates a MutableLiveData initialized with the given value
    val isDatabaseEmpty: MutableLiveData<Boolean> = MutableLiveData(false)

    fun setMutableLiveData(dataList: List<ToDoData>) {
        isDatabaseEmpty.value = dataList.isEmpty()
    }

    fun parseStringToPriority(string: String): Priority = when (string) {
        "Medium Priority" -> Priority.MEDIUM
        "Low Priority" -> Priority.LOW
        else -> Priority.HIGH
    }

    fun parsePriorityToInt(priority: Priority): Int = when (priority) {
        Priority.HIGH -> 0
        Priority.MEDIUM -> 1
        Priority.LOW -> 2
    }

}