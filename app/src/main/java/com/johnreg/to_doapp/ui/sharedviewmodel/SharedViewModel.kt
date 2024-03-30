package com.johnreg.to_doapp.ui.sharedviewmodel

import android.app.Application
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.material.snackbar.Snackbar
import com.johnreg.to_doapp.R
import com.johnreg.to_doapp.data.models.Priority
import com.johnreg.to_doapp.data.models.ToDoData

class SharedViewModel(application: Application) : AndroidViewModel(application) {

    /** ============================= List Fragment ============================= */

    // Creates a MutableLiveData initialized with the given value
    private val _isDatabaseEmpty: MutableLiveData<Boolean> = MutableLiveData(false)
    val isDatabaseEmpty: LiveData<Boolean> get() = _isDatabaseEmpty

    fun setMutableLiveData(dataList: List<ToDoData>) {
        _isDatabaseEmpty.value = dataList.isEmpty()
    }

    /** ============================= Add/Update Fragment ============================= */

    val spinnerListener: OnItemSelectedListener = object : OnItemSelectedListener {
        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            val textOfSpinner = parent?.getChildAt(0) as? TextView
            val color = when (position) {
                1 -> ContextCompat.getColor(application, R.color.yellow)
                2 -> ContextCompat.getColor(application, R.color.green)
                else -> ContextCompat.getColor(application, R.color.red)
            }
            textOfSpinner?.setTextColor(color)
        }
        override fun onNothingSelected(parent: AdapterView<*>?) {}
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

    fun showSnackbarAndDismiss(text: String, view: View) {
        val snackbar = Snackbar.make(view, text, Snackbar.LENGTH_LONG)
        snackbar.setAction("Dismiss") { snackbar.dismiss() }
        snackbar.show()
    }

}